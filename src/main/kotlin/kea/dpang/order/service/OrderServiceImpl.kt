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
import kea.dpang.order.feign.dto.ItemInfoDto
import kea.dpang.order.feign.dto.UpdateStockListRequestDto
import kea.dpang.order.feign.dto.UpdateStockRequestDto
import kea.dpang.order.feign.dto.UserDto
import kea.dpang.order.repository.OrderRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class OrderServiceImpl(
    private val itemService: ItemService,
    private val mileageService: MileageService,
    private val userService: UserService,
    private val orderRepository: OrderRepository
) : OrderService {

    private val log = LoggerFactory.getLogger(OrderServiceImpl::class.java)

    override fun placeOrder(userId: Long, orderRequest: OrderRequestDto): OrderResponseDto {
        log.info("주문 시작. 사용자 ID: {}, 주문 정보: {}", userId, orderRequest)

        // OrderRequestDto에서 주문 정보를 추출한다.
        val productInfoList = orderRequest.orderItemInfo

        // 상품 정보를 조회한다.
        val productIds = productInfoList.map { it.itemId }
        val products = itemService.getItemInfos(productIds)

        // 상품의 총 비용을 계산한다.
        val itemCost = calculateItemTotalCost(productInfoList, products)
        log.info("총 비용: {}", itemCost)

        // 사용자의 마일리지를 조회한다.
        val userMileage = mileageService.getUserMileage(userId)

        // 추후 배송비가 할인 혹은 무료 배송 등을 고려하게 되면, 배송비 계산 로직 필요
        val deliveryFee = 3_000

        // 사용자의 마일리지가 총 비용보다 많은지 확인한다.
        checkUserHasEnoughMileage(userId, itemCost + deliveryFee, userMileage)

        // 주문 정보를 생성한다.
        val order = createOrder(userId, orderRequest, itemCost)

        // 주문 정보를 데이터베이스에 저장한다.
        orderRepository.save(order)
        log.info("주문 정보 저장 완료. 주문 ID: {}", order.id)

        // 주문이 성공적으로 처리되면, 주문 상품의 재고를 감소시키고,
        itemService.updateStockInfo(
            UpdateStockListRequestDto(
                productInfoList.map { productInfo ->
                    UpdateStockRequestDto(
                        itemId = productInfo.itemId,
                        quantity = -productInfo.quantity
                    )
                }
            )
        )

        // 사용자의 마일리지를 감소시킨다.
        mileageService.consumeUserMileage(userId, itemCost + deliveryFee, "주문 결제")

        // 주문 상태를 '결제 완료'로 변경한다.
        order.details.forEach { it.status = PAYMENT_COMPLETED }

        log.info("주문 완료. 주문 ID: {}", order.id)

        // 사용자의 남은 마일리지를 조회한다.
        val remainingMileage = mileageService.getUserMileageInfo(userId)

        // 주문 응답 정보를 생성하여 반환한다.
        return OrderResponseDto.from(
            order = convertOrderEntityToDto(order),
            mileageDto = remainingMileage
        )
    }

    /**
     * 상품 정보를 사용하여 주문 상품의 총 비용을 계산하는 메서드
     *
     * @param productInfoList 주문 상품 정보 목록
     * @param products 상품 정보 목록
     * @return 주문의 총 비용
     */
    private fun calculateItemTotalCost(productInfoList: List<ItemInfo>, products: List<ItemInfoDto>): Int {
        return productInfoList.sumOf { productInfo ->

            // 상품 정보를 찾는다.
            val product = products.find { it.id == productInfo.itemId }
                ?: throw ProductNotFoundException(productInfo.itemId)

            // 상품의 재고가 충분한지 확인한다.
            if (product.quantity < productInfo.quantity) {
                throw InsufficientStockException(product.id)
            }

            // 상품의 가격과 주문 수량을 곱하여 반환한다.
            product.price * productInfo.quantity
        }
    }

    /**
     * 사용자가 충분한 마일리지를 가지고 있는지 확인하는 메서드
     *
     * @param userId 사용자 ID
     * @param totalCost 주문의 총 비용
     * @param userMileage 사용자의 마일리지
     */
    private fun checkUserHasEnoughMileage(userId: Long, totalCost: Int, userMileage: Int) {
        if (userMileage < totalCost) {
            log.error("마일리지 부족. 사용자 ID: {}, 필요 마일리지: {}, 보유 마일리지: {}", userId, totalCost, userMileage)
            throw InsufficientMileageException(userId)
        }
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

        // 상품 정보를 조회한다.
        val itemIds = orderRequestDto.orderItemInfo.map { it.itemId }.distinct()
        val itemList = itemService.getItemInfos(itemIds)

        // 주문 상세 정보를 생성하고 주문 객체에 설정한다.
        order.details = orderRequestDto.orderItemInfo.map { orderItemInfo ->

            // 상품 정보를 찾는다.
            val itemInfo = itemList.find { it.id == orderItemInfo.itemId }

            // 주문 상세 정보를 생성한다.
            OrderDetail(
                order = order, // order 객체에 대한 참조를 설정한다.
                status = ORDER_RECEIVED,
                itemId = orderItemInfo.itemId,
                quantity = orderItemInfo.quantity,
                purchasePrice = itemInfo!!.price
            )

        }.toMutableList()

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
            log.info("주문 상태 변경 완료. 주문 상세 ID: {}, 변경된 상태: {}", orderDetail.id, updateOrderStatusRequest.status)
        }
    }

    override fun updateOrderDetailStatus(orderId: Long, orderDetailId: Long, updateOrderStatusRequest: UpdateOrderStatusRequestDto) {
        log.info("주문 상태 변경 시작. 주문 ID: {}, 주문 상세 ID: {}, 변경 요청 정보: {}", orderId, orderDetailId, updateOrderStatusRequest)

        // 데이터베이스에서 주문 정보를 조회한다.
        val order = orderRepository.findById(orderId)
            .orElseThrow {
                log.error("주문 상태 변경 실패. 찾을 수 없는 주문 ID: {}", orderId)
                OrderNotFoundException(orderId)
            }

        // 주문 상세 정보를 조회한다.
        val orderDetail = order.details.find { it.id == orderDetailId }
            ?: throw OrderDetailNotFoundException(orderDetailId)

        // 주문 상태 변경을 처리한다.
        log.info("주문 상태 변경 처리 시작. 주문 상세 ID: {}, 변경 요청 정보: {}", orderDetailId, updateOrderStatusRequest)
        updateOrderDetailStatus(orderDetail, updateOrderStatusRequest)
        log.info("주문 상태 변경 완료. 주문 상세 ID: {}, 변경된 상태: {}", orderDetail.id, updateOrderStatusRequest.status)
    }

    private fun updateOrderDetailStatus(orderDetail: OrderDetail, updateOrderStatusRequest: UpdateOrderStatusRequestDto) {
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
    }

    /**
     * 주문 상태 변경이 유효한지 검증하는 메서드.
     * '결제 완료' 상태 이후로는 '결제 완료' 이전 상태로 돌아갈 수 없고,
     * '주문 취소' 이후에는 그 어떤 상태로의 변경도 할 수 없다.
     *
     * @param currentStatus 현재 주문 상태
     * @param targetStatus 변경하려는 주문 상태
     * @throws InvalidOrderStatusChangeException 주문 상태 변경이 유효하지 않은 경우
     */
    private fun validateStatusChange(currentStatus: OrderStatus, targetStatus: OrderStatus) {
        log.info("주문 상태 변경 검증 시작. 현재 상태: {}, 변경하려는 상태: {}", currentStatus, targetStatus)

        if (currentStatus == OrderStatus.CANCELLED) {
            log.error("주문 취소 상태에서는 주문 상태 변경이 불가능합니다. 현재 상태: {}, 변경하려는 상태: {}", currentStatus, targetStatus)
            throw InvalidOrderStatusChangeException(currentStatus.name, targetStatus.name)
        }

        if (currentStatus.ordinal > PAYMENT_COMPLETED.ordinal
            && targetStatus.ordinal <= PAYMENT_COMPLETED.ordinal) {
            log.error("결제 완료 상태 이후로는 결제 완료 이전 상태로 돌아갈 수 없습니다. 현재 상태: {}, 변경하려는 상태: {}", currentStatus, targetStatus)
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

        val orders = orderRepository
            .findOrders(startDate, endDate, userId, pageable)

        log.info("주문 목록 조회 완료. 조회된 주문 건수: {}", orders.totalElements)

        // 주문 목록이 비어있으면 빈 페이지를 반환한다.
        if (orders.isEmpty) {
            return Page.empty(pageable)
        }

        // 주문 목록에 포함된 상품 ID와 사용자 ID를 추출한다.
        val itemIds = orders.content.flatMap { it.details.map { detail -> detail.itemId } }.distinct()
        val userIds = orders.content.map { it.userId }.distinct()

        log.info("주문 목록에 포함된 상품 ID: {}, 사용자 ID: {}", itemIds, userIds)

        // runBlocking 블록 내에서 비동기로 상품 정보 조회와 사용자 정보 조회한다.
        val (items, users) = runBlocking {
            val itemsDeferred = async {
                itemService.getItemInfos(itemIds).associateBy { it.id }
            }

            val usersDeferred = async {
                userService.getUserInfos(userIds).associateBy { it.userId }
            }

            // itemsDeferred와 usersDeferred가 모두 완료될 때까지 기다린 후, 그 결과를 Pair로 묶어서 반환한다.
            Pair(itemsDeferred.await(), usersDeferred.await())
        }

        return orders.map { convertOrderEntityToDto(it, users, items) }
    }

    /**
     * Order 엔티티를 OrderDto로 변환하는 메서드
     *
     * @param order 변환할 Order 엔티티 객체
     * @param users 주문에 포함된 사용자 정보
     * @param items 주문에 포함된 상품 정보
     * @return 변환된 OrderDto 객체
     */
    private fun convertOrderEntityToDto(
        order: Order,
        users: Map<Long, UserDto>? = null,
        items: Map<Long, ItemInfoDto>? = null
    ): OrderDto {
        // 상품 정보가 넘어오지 않았다면 상품 서비스에 요청하여 상품 정보를 조회한다.
        val itemList = items ?: run {
            val itemIds = order.details.map { it.itemId }.distinct()
            itemService.getItemInfos(itemIds).associateBy { it.id }
        }

        // 주문 상품 정보를 변환한다.
        val productList = order.details.map { orderDetail ->
            val productInfo = itemList.getValue(orderDetail.itemId)
            OrderedProductInfo(
                orderDetailId = orderDetail.id!!,
                orderStatus = orderDetail.status,
                productInfoDto = ProductInfoDto.from(productInfo),
                productQuantity = orderDetail.quantity
            )
        }

        // 사용자 정보가 넘어오지 않았다면 사용자 서비스에 요청하여 사용자 정보를 조회한다.
        val orderer = users?.getValue(order.userId)?.name ?: run {
            val userId = order.userId
            userService.getUserInfo(userId).name
        }

        return OrderDto(
            orderId = order.id!!,
            orderDate = order.date!!.toLocalDate(),
            productList = productList,
            orderer = orderer
        )
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

        // 상품 정보 조회
        val productInfo = itemService.getItemInfo(orderDetail.itemId)

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
