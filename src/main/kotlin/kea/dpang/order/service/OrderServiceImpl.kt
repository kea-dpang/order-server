package kea.dpang.order.service

import kea.dpang.order.dto.OrderedProductInfo
import kea.dpang.order.dto.ProductInfoDto
import kea.dpang.order.dto.order.*
import kea.dpang.order.entity.Order
import kea.dpang.order.entity.OrderDetail
import kea.dpang.order.entity.OrderRecipient
import kea.dpang.order.entity.OrderStatus
import kea.dpang.order.entity.OrderStatus.ORDER_RECEIVED
import kea.dpang.order.entity.OrderStatus.PAYMENT_COMPLETED
import kea.dpang.order.exception.*
import kea.dpang.order.feign.ItemServiceFeignClient
import kea.dpang.order.feign.MileageServiceFeignClient
import kea.dpang.order.feign.dto.ConsumeMileageRequestDto
import kea.dpang.order.feign.dto.UpdateStockListRequestDto
import kea.dpang.order.feign.dto.UpdateStockRequestDto
import kea.dpang.order.repository.OrderDetailRepository
import kea.dpang.order.repository.OrderRepository
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(OrderServiceImpl::class.java)

    override fun placeOrder(userId: Long, orderRequest: OrderRequestDto): OrderDto {
        log.info("주문 시작. 사용자 ID: {}, 주문 정보: {}", userId, orderRequest)

        // OrderRequestDto에서 주문 정보를 추출한다.
        val productInfoList = orderRequest.orderIteminfo

        // 주문 상품의 재고가 충분한지 확인하고, 총 비용을 계산한다.
        var totalCost = 0
        for (productInfo in productInfoList) {
            val productId = productInfo.itemId
            val quantity = productInfo.quantity

            // 상품 정보를 받아온다.
            val product = itemServiceFeignClient.getItemInfo(productId).body!!.data
            log.info("상품 정보 조회 완료. 상품 ID: {}, 상품 정보: {}", productId, product)

            if (product.quantity < quantity) {
                log.error("재고 부족. 상품 ID: {}, 요청량: {}, 재고량: {}", productId, quantity, product.quantity)
                throw InsufficientStockException(productId)
            }

            totalCost += product.price * quantity
        }

        // 사용자의 마일리지가 총 비용보다 많은지 확인한다.
        val response = mileageServiceFeignClient.getUserMileage(userId, userId)
        val mileage = response.body!!.data.mileage
        val personalChargedMileage = response.body!!.data.personalChargedMileage
        val userTotalMileage = mileage + personalChargedMileage

        log.info("마일리지 조회 완료. 사용자 ID: {}, 총 마일리지: {} (마일리지: {}, 충전 마일리지: {})", userId, userTotalMileage, mileage, personalChargedMileage)

        if (userTotalMileage < totalCost) {
            log.error("마일리지 부족. 사용자 ID: {}, 필요 마일리지: {}, 보유 마일리지: {}", userId, totalCost, userTotalMileage)
            throw InsufficientMileageException(userId)
        }

        // 주문 정보를 생성한다.
        val order = createOrder(userId, orderRequest, totalCost)

        // 주문 정보를 데이터베이스에 저장한다.
        orderRepository.save(order)
        log.info("주문 정보 저장 완료. 주문 ID: {}", order.id)

        // 주문이 성공적으로 처리되면, 주문 상품의 재고를 감소시키고,
        val dto = UpdateStockListRequestDto(
            productInfoList.map { productInfo ->
                UpdateStockRequestDto(
                    itemId = productInfo.itemId,
                    quantity = -productInfo.quantity
                )
            }
        )

        itemServiceFeignClient.updateStock(dto)
        log.info("상품 재고 감소 요청 완료.")

        // 사용자의 마일리지를 감소시킨다.
        val consumeMileageRequest = ConsumeMileageRequestDto(
            userId = userId,
            amount = totalCost,
            reason = "주문 결제"
        )
        mileageServiceFeignClient.consumeMileage(userId, consumeMileageRequest)
        log.info("마일리지 사용 요청 완료. 사용자 ID: {}, 사용량: {}", userId, totalCost)

        // 주문 상태를 '결제 완료'로 변경한다.
        order.details.forEach { orderDetail ->
            orderDetail.status = PAYMENT_COMPLETED
        }

        log.info("주문 완료. 주문 ID: {}", order.id)

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
        log.info("주문 생성 시작. 사용자 ID: {}, 주문 요청 정보: {}, 총 비용: {}", userId, orderRequestDto, totalCost)

        // 주문 객체를 생성한다. 초기에 수령인 정보(recipient)는 설정하지 않는다.
        val order = Order(
            userId = userId,
            deliveryRequest = orderRequestDto.deliveryRequest,
            productPaymentAmount = totalCost,
            recipient = null,  // 초기에는 null로 설정한다.
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
        log.info("수령인 정보 설정 완료. 사용자 ID: {}", userId)

        // 주문 상세 정보를 생성하고 주문 객체에 설정한다.
        order.details = orderRequestDto.orderIteminfo.map { orderIteminfo ->

            // 상품 서비스로부터 상품 정보를 조회한다.
            val itemInfo = itemServiceFeignClient.getItemInfo(orderIteminfo.itemId).body!!.data

            // 주문 상세 정보를 생성한다.
            OrderDetail(
                order = order, // order 객체에 대한 참조를 설정한다.
                status = ORDER_RECEIVED,
                itemId = orderIteminfo.itemId,
                quantity = orderIteminfo.quantity,
                purchasePrice = itemInfo.price
            )

        }.toMutableList()
        log.info("주문 상세 정보 설정 완료. 사용자 ID: {}", userId)

        log.info("주문 생성 완료. 사용자 ID: {}", userId)

        // 생성된 주문 객체를 반환한다.
        return order
    }

    override fun updateOrderStatus(orderId: Long, updateOrderStatusRequest: UpdateOrderStatusRequestDto) {
        log.info("주문 상태 변경 시작. 주문 ID: {}, 변경 요청 정보: {}", orderId, updateOrderStatusRequest)

        // 데이터베이스에서 주문 정보를 조회한다.
        val order = orderRepository.findById(orderId)
            .orElseThrow {
                log.error("주문 상태 변경 실패. 찾을 수 없는 주문 상세 ID: {}", orderId)
                OrderNotFoundException(orderId)
            }

        // 주문 상태 변경을 처리한다.
        order.details.forEach { orderDetail ->
            log.info("주문 상태 변경 처리 시작. 주문 상세 ID: {}, 변경 요청 정보: {}", orderDetail.id, updateOrderStatusRequest)
            updateOrderDetailStatus(orderDetail, updateOrderStatusRequest)
        }
    }

    override fun updateOrderDetailStatus(orderDetailId: Long, updateOrderStatusRequest: UpdateOrderStatusRequestDto) {
        log.info("주문 상태 변경 시작. 주문 상세 ID: {}, 변경 요청 정보: {}", orderDetailId, updateOrderStatusRequest)

        // 데이터베이스에서 주문 정보를 조회한다.
        val orderDetail = orderDetailRepository.findById(orderDetailId)
            .orElseThrow {
                log.error("주문 상태 변경 실패. 찾을 수 없는 주문 상세 ID: {}", orderDetailId)
                OrderNotFoundException(orderDetailId)
            }

        // 주문 상태 변경을 처리한다.
        log.info("주문 상태 변경 처리 시작. 주문 상세 ID: {}, 변경 요청 정보: {}", orderDetailId, updateOrderStatusRequest)
        updateOrderDetailStatus(orderDetail, updateOrderStatusRequest)
    }

    fun updateOrderDetailStatus(orderDetail: OrderDetail, updateOrderStatusRequest: UpdateOrderStatusRequestDto) {
        // 변경할 주문 상태를 추출한다.
        val targetStatus = updateOrderStatusRequest.status

        // 변경할 주문 상태가 현재 주문 상태와 동일한지 확인한다.
        if (orderDetail.status == targetStatus) {
            log.error("주문 상태 변경 실패. 이미 요청된 상태입니다. 주문 상세 ID: {}, 현재 상태: {}, 요청 상태: {}", orderDetail.id, orderDetail.status, targetStatus)
            throw OrderAlreadyInRequestedStatusException()
        }

        // 주문 상태 변경이 유효한지 검증한다.
        validateStatusChange(orderDetail.status, targetStatus)

        // 주문 정보의 상태를 변경한다.
        orderDetail.status = targetStatus

        log.info("주문 상태 변경 완료. 주문 상세 ID: {}, 변경된 상태: {}", orderDetail.id, targetStatus)
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
        log.info("주문 상태 변경 검증 시작. 현재 상태: {}, 변경하려는 상태: {}", currentStatus, targetStatus)

        if (currentStatus.ordinal + 1 != targetStatus.ordinal) {
            log.error("주문 상태 변경 검증 실패. 현재 상태: {}, 변경하려는 상태: {}", currentStatus, targetStatus)
            throw InvalidOrderStatusChangeException(currentStatus.name, targetStatus.name)
        }

        log.info("주문 상태 변경 검증 완료. 현재 상태: {}, 변경하려는 상태: {}", currentStatus, targetStatus)
    }

    @Transactional(readOnly = true)
    override fun getOrderList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        userId: Long?,
        pageable: Pageable
    ): Page<OrderDto> {
        log.info("주문 목록 조회 시작. 시작 날짜: {}, 종료 날짜: {}, 사용자 ID: {}, 페이지 정보: {}", startDate, endDate, userId, pageable)

        val orderList = orderRepository
            .findOrders(startDate, endDate, userId, pageable)
            .map { convertOrderEntityToDto(it) }

        log.info("주문 목록 조회 완료. 조회된 주문 건수: {}", orderList.totalElements)

        return orderList
    }


    /**
     * Order 엔티티를 OrderDto로 변환하는 메서드
     *
     * @param order 변환할 Order 엔티티 객체
     * @return 변환된 OrderDto 객체
     */
    private fun convertOrderEntityToDto(order: Order): OrderDto {
        log.info("Order 엔티티를 OrderDto로 변환 시작. 변환할 Order ID: {}", order.id)

        // 상품 서비스로부터 상품 정보 조회 및 DTO 설정
        val productList = order.details.map { orderDetail ->
            val productInfo = itemServiceFeignClient.getItemInfo(orderDetail.itemId).body!!.data // 상품 정보 가져오기

            OrderedProductInfo(
                orderDetailId = orderDetail.id!!,
                orderStatus = orderDetail.status,
                productInfoDto = ProductInfoDto.from(productInfo),
                productQuantity = orderDetail.quantity
            )
        }

        // OrderDto로 변환 및 반환
        val orderDto = OrderDto(
            orderId = order.id!!,
            orderDate = order.date!!.toLocalDate(),
            productList = productList,
            orderer = order.userId.toString()
        )

        log.info("Order 엔티티를 OrderDto로 변환 완료. 변환된 OrderDto ID: {}", orderDto.orderId)

        return orderDto
    }

    @Transactional(readOnly = true)
    override fun getOrderInfo(orderId: Long): OrderDetailDto {
        log.info("주문 상세 정보 조회 시작. 주문 ID: {}", orderId)

        // 데이터베이스에서 주문 정보를 조회
        val order = orderRepository.findById(orderId)
            .orElseThrow {
                log.error("주문 상세 정보 조회 실패. 찾을 수 없는 주문 ID: {}", orderId)
                OrderNotFoundException(orderId)
            }

        // OrderDetailDto 반환
        val orderDetailDto = OrderDetailDto(
            order = convertOrderEntityToDto(order),
            deliveryInfo = DeliveryInfo.from(order.recipient!!),
            paymentInfo = PaymentInfo.from(order),
            deliveryRequest = order.deliveryRequest
        )

        log.info("주문 상세 정보 조회 완료. 주문 ID: {}", orderId)

        return orderDetailDto
    }

    @Transactional(readOnly = true)
    override fun getOrderDetailInfo(orderId: Long, orderDetailId: Long): OrderedProductInfo {
        log.info("주문 상세 정보 조회 시작. 주문 ID: {}, 주문 상세 ID: {}", orderId, orderDetailId)

        // 데이터베이스에서 주문 정보를 조회
        val order = orderRepository.findById(orderId)
            .orElseThrow {
                log.error("주문 상세 정보 조회 실패. 찾을 수 없는 주문 ID: {}", orderId)
                OrderNotFoundException(orderId)
            }

        // 주문 상세 정보를 조회
        val orderDetail = order.details.find { it.id == orderDetailId }
            ?: throw OrderDetailNotFoundException(orderDetailId)

        // 상품 서비스로부터 상품 정보 조회
        val productInfo = itemServiceFeignClient.getItemInfo(orderDetail.itemId).body!!.data

        // OrderedProductInfo로 변환
        val orderedProductInfo = OrderedProductInfo(
            orderDetailId = orderDetail.id!!,
            orderStatus = orderDetail.status,
            productInfoDto = ProductInfoDto.from(productInfo),
            productQuantity = orderDetail.quantity
        )

        log.info("주문 상세 정보 조회 완료. 주문 ID: {}, 주문 상세 ID: {}", orderId, orderDetailId)

        return orderedProductInfo
    }
}
