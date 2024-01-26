package kea.dpang.order.service

import kea.dpang.order.dto.OrderedProductInfo
import kea.dpang.order.dto.ProductInfoDto
import kea.dpang.order.dto.order.*
import kea.dpang.order.entity.*
import kea.dpang.order.entity.OrderStatus.*
import kea.dpang.order.exception.*
import kea.dpang.order.feign.ItemServiceFeignClient
import kea.dpang.order.feign.MileageServiceFeignClient
import kea.dpang.order.feign.dto.ConsumeMileageRequestDto
import kea.dpang.order.repository.OrderDetailRepository
import kea.dpang.order.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderDetailRepository: OrderDetailRepository,
    private val itemServiceFeignClient: ItemServiceFeignClient,
    private val mileageServiceFeignClient: MileageServiceFeignClient
) : OrderService {

    override fun placeOrder(userId: Long, orderRequest: OrderRequestDto): OrderDto {
        // OrderRequestDto에서 주문 정보를 추출한다.
        val productInfoList = orderRequest.orderIteminfo

        // 주문 상품의 재고가 충분한지 확인하고, 총 비용을 계산한다.
        var totalCost = 0
        for (productInfo in productInfoList) {
            val productId = productInfo.itemId
            val quantity = productInfo.quantity

            // 상품 정보를 받아온다.
            val product = itemServiceFeignClient.getItemInfo(productId).data
            if (product.quantity < quantity) {
                throw InsufficientStockException(productId)
            }

            totalCost += product.price * quantity
        }

        // 사용자의 마일리지가 총 비용보다 많은지 확인한다.
        val response = mileageServiceFeignClient.getUserMileage(userId, userId)
        if (response.data.mileage < totalCost) {
            throw InsufficientMileageException(userId)
        }

        // 주문 정보를 생성한다.
        val order = createOrder(userId, orderRequest, totalCost)

        // 주문 정보를 데이터베이스에 저장한다.
        orderRepository.save(order)

        // 주문이 성공적으로 처리되면, 주문 상품의 재고를 감소시키고,
        for (productInfo in productInfoList) {
            val productId = productInfo.itemId
            val quantity = productInfo.quantity

            itemServiceFeignClient.decreaseItemStock(productId, quantity)
        }

        // 사용자의 마일리지를 감소시킨다.
        val consumeMileageRequest = ConsumeMileageRequestDto(
            userId = userId,
            amount = totalCost,
            reason = "주문 결제"
        )
        mileageServiceFeignClient.consumeMileage(userId, consumeMileageRequest)

        // 주문 상태를 '결제 완료'로 변경한다.
        order.details.forEach { orderDetail ->
            orderDetail.status = PAYMENT_COMPLETED
        }

        // 저장된 주문 정보를 반환한다.
        return convertOrderEntityToDto(order)
    }

    /**
     * 주문을 생성하는 메소드
     *
     * @param userId 주문을 생성하는 사용자의 ID.
     * @param orderRequestDto 주문 생성 요청 정보를 담고 있는 DTO.
     * @param totalCost 상품의 총 가격.
     * @return 생성된 Order 엔티티.
     */
    private fun createOrder(userId: Long, orderRequestDto: OrderRequestDto, totalCost: Int): Order {
        // 주문 객체를 생성한다. 초기에 수령인 정보(recipient)는 설정하지 않는다.
        val order = Order(
            userId = userId,
            deliveryRequest = orderRequestDto.deliveryRequest,
            productPaymentAmount = totalCost,
            recipient = null,  // 초기에는 null로 설정합니다.
            details = mutableListOf()
        )

        // 수령인 정보를 생성하고 주문 객체에 설정한다.
        val orderRecipient = OrderRecipient(
            order = order,
            name = orderRequestDto.deliveryInfo.name,
            phoneNumber = orderRequestDto.deliveryInfo.phoneNumber,
            zipCode = orderRequestDto.deliveryInfo.zipCode,
            address = orderRequestDto.deliveryInfo.address,
            detailAddress = orderRequestDto.deliveryInfo.detailAddress
        )
        order.updateRecipient(orderRecipient)

        // 주문 상세 정보를 생성하고 주문 객체에 설정한다.
        order.details = orderRequestDto.orderIteminfo.map { orderIteminfo ->

            // 상품 서비스로부터 상품 정보를 조회한다.
            val itemInfo = itemServiceFeignClient.getItemInfo(orderIteminfo.itemId).data

            // 주문 상세 정보를 생성한다.
            OrderDetail(
                order = order, // order 객체에 대한 참조를 설정한다.
                status = ORDER_RECEIVED,
                itemId = orderIteminfo.itemId,
                quantity = orderIteminfo.quantity,
                purchasePrice = itemInfo.price
            )

        }.toMutableList()

        // 생성된 주문 객체를 반환한다.
        return order
    }

    override fun updateOrderStatus(orderDetailId: Long, updateOrderStatusRequest: UpdateOrderStatusRequestDto) {
        // 데이터베이스에서 주문 정보를 조회한다.
        val order = orderDetailRepository.findById(orderDetailId)
            .orElseThrow { OrderNotFoundException(orderDetailId) }

        // 변경할 주문 상태를 추출한다.
        val targetStatus = updateOrderStatusRequest.status

        // 변경할 주문 상태가 현재 주문 상태와 동일한지 확인한다.
        if (order.status == targetStatus) {
            throw OrderAlreadyInRequestedStatusException()
        }

        // 주문 상태 변경이 유효한지 검증한다.
        validateStatusChange(order.status, targetStatus)

        // 주문 정보의 상태를 변경한다.
        order.status = targetStatus
    }

    /**
     * 주문 상태 변경이 유효한지 검증하는 메서드.
     * 주문 상태는 순차적으로 변경되어야 하며, 이를 위반할 경우 예외를 발생시킨다.
     * 예를 들어, '주문 접수' 상태에서 바로 '배송중' 상태로 변경하는 것은 허용되지 않는다.
     *
     * @param currentStatus 현재 주문 상태
     * @param targetStatus 변경하려는 주문 상태
     * @throws InvalidOrderStatusChangeException 주문 상태 변경이 유효하지 않은 경우
     */
    private fun validateStatusChange(currentStatus: OrderStatus, targetStatus: OrderStatus) {
        if (currentStatus.ordinal + 1 != targetStatus.ordinal) {
            throw InvalidOrderStatusChangeException(currentStatus.name, targetStatus.name)
        }
    }

    @Transactional(readOnly = true)
    override fun getOrderList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        orderId: Long?,
        pageable: Pageable
    ): Page<OrderDto> {

        return orderRepository
            .findOrders(startDate, endDate, orderId, pageable)
            .map { convertOrderEntityToDto(it) }
    }

    /**
     * Order 엔티티를 OrderDto로 변환하는 메서드
     *
     * @param order 변환할 Order 엔티티 객체
     * @return 변환된 OrderDto 객체
     */
    private fun convertOrderEntityToDto(order: Order): OrderDto {
        // 상품 서비스로부터 상품 정보 조회 및 DTO 설정
        val productList = order.details.map { orderDetail ->
            val productInfo = itemServiceFeignClient.getItemInfo(orderDetail.itemId).data // 상품 정보 가져오기

            OrderedProductInfo(
                orderDetailId = orderDetail.id!!,
                orderStatus = orderDetail.status,
                productInfoDto = ProductInfoDto.from(productInfo),
                productQuantity = orderDetail.quantity
            )
        }

        // OrderDto로 변환 및 반환
        return OrderDto(
            orderId = order.id!!,
            orderDate = order.date!!.toLocalDate(),
            productList = productList,
            orderer = order.userId.toString()
        )
    }

    @Transactional(readOnly = true)
    override fun getOrder(orderId: Long): OrderDetailDto {
        // 데이터베이스에서 주문 정보를 조회
        val order = orderRepository.findById(orderId)
            .orElseThrow { OrderNotFoundException(orderId) }

        // OrderDetailDto 반환
        return OrderDetailDto(
            order = convertOrderEntityToDto(order),
            deliveryInfo = DeliveryInfo.from(order.recipient!!),
            paymentInfo = PaymentInfo.from(order)
        )
    }

}
