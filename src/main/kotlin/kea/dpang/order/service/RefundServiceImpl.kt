package kea.dpang.order.service

import kea.dpang.order.dto.OrderedProductInfo
import kea.dpang.order.dto.ProductInfoDto
import kea.dpang.order.dto.refund.*
import kea.dpang.order.entity.OrderDetail
import kea.dpang.order.entity.OrderStatus.CANCELLED
import kea.dpang.order.entity.OrderStatus.DELIVERY_COMPLETED
import kea.dpang.order.entity.Recall
import kea.dpang.order.entity.Refund
import kea.dpang.order.entity.RefundStatus
import kea.dpang.order.exception.InvalidRefundStatusChangeException
import kea.dpang.order.exception.RefundAlreadyInRequestedStatusException
import kea.dpang.order.exception.RefundNotFoundException
import kea.dpang.order.exception.UnableToRefundException
import kea.dpang.order.feign.dto.ItemInfoDto
import kea.dpang.order.feign.dto.UpdateStockListRequestDto
import kea.dpang.order.feign.dto.UpdateStockRequestDto
import kea.dpang.order.feign.dto.UserDto
import kea.dpang.order.repository.OrderRepository
import kea.dpang.order.repository.RefundRepository
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class RefundServiceImpl(
    private val itemService: ItemService,
    private val mileageService: MileageService,
    private val userService: UserService,
    private val refundRepository: RefundRepository,
    orderRepository: OrderRepository
) : RefundService, BaseService(itemService, userService, orderRepository) {

    private val log = LoggerFactory.getLogger(RefundServiceImpl::class.java)

    override fun refundOrder(orderId: Long, orderDetailId: Long, refundOrderRequest: RefundOrderRequestDto) {
        log.info("환불 요청 시작. 주문 상세 ID: {}", orderDetailId)

        // 주문 상세 정보를 조회한다.
        val orderDetail = fetchOrderDetail(orderId, orderDetailId)

        // 조회된 주문 상태를 확인하여, 환불이 가능한 상태인지 확인한다.
        checkRefundAvailable(orderDetail)

        // 주문 상태를 '취소'로 변경한다.
        orderDetail.status = CANCELLED

        // RefundOrderRequestDto에서 환불 요청 정보를 추출하고 환불, 회수 객체를 생성한다.
        val refund = Refund(
            orderDetail = orderDetail,
            refundReason = refundOrderRequest.refundReason,
            note = refundOrderRequest.remark,
            refundAmount = orderDetail.purchasePrice,
            refundStatus = RefundStatus.REFUND_REQUEST,
            recall = null
        )

        val recall = Recall(
            refund = null, // 임의로 refund를 null로 설정
            retrieverName = orderDetail.order.recipient!!.name,
            retrieverPhoneNumber = orderDetail.order.recipient!!.phoneNumber,
            retrieverAddress = "(${orderDetail.order.recipient!!.zipCode}) ${orderDetail.order.recipient!!.address} ${orderDetail.order.recipient!!.detailAddress}",
            retrievalMessage = refundOrderRequest.retrievalMessage
        )

        // 환불, 회수 객체를 서로 연관 관계로 설정한다.
        refund.assignRecall(recall)

        // 환불 요청 정보 및 회수 정보를 데이터베이스에 저장한다.
        // CascadeType.ALL 설정으로 인해 Recall 엔티티는 Refund 엔티티와 함께 자동으로 처리된다.
        refundRepository.save(refund)
        log.info("환불 정보 저장 완료. 주문 상세 ID: {}", orderDetailId)

        // 주문 상세 정보에 환불 정보를 연관 관계 편의 메서드를 사용하여 추가한다.
        orderDetail.assignRefund(refund)

        log.info("환불 요청 완료. 주문 상세 ID: {}", orderDetailId)
    }

    /**
     * 주문 상세 정보의 상태를 확인하여 환불이 가능한지 확인하는 메서드
     *
     * @param orderDetail 환불 가능 여부를 확인할 주문 상세 정보
     */
    private fun checkRefundAvailable(orderDetail: OrderDetail) {
        if (orderDetail.status != DELIVERY_COMPLETED) {
            log.error("환불 불가능 상태. 주문 상세 ID: {}", orderDetail.id)
            throw UnableToRefundException()
        }
    }

    @Transactional(readOnly = true)
    override fun getRefund(refundId: Long): RefundDetailDto {
        log.info("환불 상세 정보 조회 시작. 환불 ID: {}", refundId)

        // orderId를 사용하여 데이터베이스에서 환불 상세 정보를 조회한다.
        val refund = refundRepository.findById(refundId)
            .orElseThrow {
                log.error("환불 정보를 찾을 수 없음. 환불 ID: {}", refundId)
                RefundNotFoundException(refundId)
            }

        log.info("환불 정보 조회 완료.")

        // 조회된 환불 상세 정보를 RefundDetailDto로 변환 및 반환한다.
        return RefundDetailDto(
            refundDto = convertRefundEntityToDto(refund),
            recallInfo = RetrievalInfo.from(refund.recall!!),
            refundInfo = RefundInfo(
                productPaymentAmount = refund.refundAmount,
                expectedRefundAmount = refund.refundAmount,
                refundStatus = refund.refundStatus
            ),
            returnInfo = ReturnInfo.from(refund)
        )
    }

    /**
     * Refund 엔티티를 RefundDto로 변환하는 메서드
     *
     * @param refund 변환할 Refund 엔티티 객체
     * @return 변환된 RefundDto 객체
     */
    fun createRefundDto(
        refund: Refund,
        user: UserDto,
        productInfoDto: ProductInfoDto
    ): RefundDto {
        val orderDetail = refund.orderDetail
        val order = orderDetail.order

        val orderedProductInfo = OrderedProductInfo(
            orderDetailId = orderDetail.id!!,
            orderStatus = orderDetail.status,
            productInfoDto = productInfoDto,
            productQuantity = orderDetail.quantity
        )

        return RefundDto(
            refundId = refund.id!!,
            refundRequestDate = refund.refundRequestDate!!.toLocalDate(),
            userId = order.userId,
            userName = user.name,
            orderId = order.id!!,
            orderDate = order.date!!.toLocalDate(),
            product = orderedProductInfo,
            expectedRefundAmount = refund.refundAmount,
            refundStatus = refund.refundStatus,
            refundReason = refund.refundReason,
        )
    }

    @Transactional(readOnly = true)
    override fun getRefundList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        refundStatus: RefundStatus?,
        userId: Long?,
        pageable: Pageable
    ): Page<RefundDto> {
        log.info("환불 목록 조회 시작. 시작 날짜: {}, 종료 날짜: {}, 환불 상태: {}, 사용자 ID: {}, 페이지 정보: {}", startDate, endDate, refundStatus, userId, pageable)

        // 환불 목록을 조회한다.
        val refunds = refundRepository.findRefunds(startDate, endDate, refundStatus, userId, pageable)
        log.info("환불 목록 조회 완료. 조회된 환불 수: {}", refunds.size)

        // 환불 목록이 비어있는 경우, 빈 페이지를 반환한다.
        if (refunds.isEmpty) {
            return Page.empty()
        }

        // 환불 목록에 포함된 상품 ID와 사용자 ID를 추출한다.
        val itemIds = refunds.map { it.orderDetail.itemId }.distinct()
        val userIds = refunds.map { it.orderDetail.order.userId }.distinct()

        log.info("환불 목록에 포함된 상품 ID 목록: {}, 사용자 ID 목록: {}", itemIds, userIds)

        // runBlocking 블록 내에서 비동기로 상품 정보 조회와 사용자 정보 조회한다.
        val (items, users) = runBlocking { fetchItemAndUserInfos(itemIds, userIds) }

        // RefundDto 목록으로 변환하여 반환한다.
        return refunds.map { convertRefundEntityToDto(it, users, items) }
    }

    /**
     * Refund 엔티티를 RefundDto로 변환하는 메서드
     *
     * @param refund 변환할 Refund 엔티티 객체
     * @param users 환불 목록에 포함된 사용자 정보
     * @param items 환불 목록에 포함된 상품 정보
     * @return 변환된 RefundDto 객체
     */
    private fun convertRefundEntityToDto(
        refund: Refund,
        users: Map<Long, UserDto>? = null,
        items: Map<Long, ItemInfoDto>? = null
    ): RefundDto {
        val orderDetail = refund.orderDetail
        val order = orderDetail.order

        // 사용자 정보가 넘어오지 않았다면 사용자 서비스에 요청하여 사용자 정보를 얻는다.
        val user = users?.get(order.userId) ?: userService.getUserInfo(order.userId)

        // 상품 정보가 넘어오지 않았다면 상품 서비스에 요청하여 상품 정보를 얻는다.
        val productInfo = items?.get(orderDetail.itemId) ?: itemService.getItemInfo(orderDetail.itemId)

        // 상품 정보를 DTO로 변환한다.
        val productInfoDto = ProductInfoDto.from(productInfo)

        return createRefundDto(refund, user, productInfoDto)
    }

    override fun updateRefundStatus(refundId: Long, refundStatusDto: UpdateRefundStatusRequestDto) {
        log.info("환불 상태 업데이트 시작. 환불 ID: {}, 새로운 환불 상태: {}", refundId, refundStatusDto.status)

        val refund = refundRepository.findById(refundId)
            .orElseThrow {
                log.error("환불 정보를 찾을 수 없음. 환불 ID: {}", refundId)
                RefundNotFoundException(refundId)
            }

        log.info("환불 정보 조회 완료. 환불 ID: {}", refundId)

        // RefundStatusDto에서 변경할 환불 상태를 추출한다.
        val newStatus = refundStatusDto.status

        // 환불 상태 변경이 유효한지 검증한다.
        validateStatusChange(refund.refundStatus, newStatus)

        // 환불 상태 변경이 유효하다면 환불 상세 정보의 상태를 변경한다.
        refund.refundStatus = newStatus

        // 변경할 환불 상태가 '반품 완료'인 경우, 주문에 사용된 마일리지를 사용자에게 환불하고, 환불된 상품의 재고를 증가시킨다.
        if (newStatus == RefundStatus.REFUND_COMPLETE) {
            log.info("환불 완료 처리 시작. 주문 상세 ID: {}", refund.orderDetail.id)

            // 반품된 주문 상세 정보
            val orderDetail = refund.orderDetail

            // 주문에 사용된 마일리지를 마일리지 서비스에 요청하여 사용자에게 환불한다.
            mileageService.refundMileage(orderDetail.order.userId, orderDetail.purchasePrice, "주문 취소")

            // 취소된 주문에 포함된 상품의 개수를 상품 서비스에 요청하여 재고를 증가시킨다.
            itemService.updateStockInfo(
                UpdateStockListRequestDto(
                    listOf(
                        UpdateStockRequestDto(
                            itemId = orderDetail.itemId,
                            quantity = orderDetail.quantity
                        )
                    )
                )
            )
        }

        log.info("환불 상태 업데이트 완료. 주문 상세 ID: {}, 새로운 환불 상태: {}", refund.orderDetail.id, newStatus)
    }

    /**
     * 환불 상태 변경이 유효한지 검증하는 메서드.
     * 환불 상태는 순차적으로 변경되어야 하며, 이를 위반할 경우 예외를 발생시킨다.
     * 예를 들어, '반품 요청' 상태에서 바로 '반품 완료' 상태로 변경하는 것은 허용되지 않는다.
     *
     * @param currentStatus 현재 환불 상태
     * @param targetStatus 변경하려는 환불 상태
     * @throws InvalidRefundStatusChangeException 환불 상태 변경이 유효하지 않은 경우
     */
    private fun validateStatusChange(currentStatus: RefundStatus, targetStatus: RefundStatus) {
        // 변경할 환불 상태가 현재 환불 상태와 동일한지 확인한다.
        if (currentStatus == targetStatus) {
            log.error("이미 요청된 환불 상태. 환불 ID: {}, 현재 상태: {}, 변경할 상태: {}", currentStatus, targetStatus)
            throw RefundAlreadyInRequestedStatusException()
        }

        // 변경할 환불 상태가 현재 환불 상태보다 순차적인지 확인한다.
        if (currentStatus.ordinal + 1 != targetStatus.ordinal) {
            log.error("잘못된 환불 상태 변경 시도. 현재 상태: {}, 목표 상태: {}", currentStatus, targetStatus)
            throw InvalidRefundStatusChangeException(currentStatus.name, targetStatus.name)
        }
    }

}
