package kea.dpang.order.service

import kea.dpang.order.dto.OrderedProductInfo
import kea.dpang.order.dto.ProductInfoDto
import kea.dpang.order.dto.refund.*
import kea.dpang.order.entity.OrderStatus.*
import kea.dpang.order.entity.Reason
import kea.dpang.order.entity.Recall
import kea.dpang.order.entity.Refund
import kea.dpang.order.entity.RefundStatus
import kea.dpang.order.exception.*
import kea.dpang.order.feign.ItemServiceFeignClient
import kea.dpang.order.feign.MileageServiceFeignClient
import kea.dpang.order.feign.dto.RefundMileageRequestDTO
import kea.dpang.order.repository.OrderDetailRepository
import kea.dpang.order.repository.RefundRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class RefundServiceImpl(
    private val refundRepository: RefundRepository,
    private val orderDetailRepository: OrderDetailRepository,
    private val itemServiceFeignClient: ItemServiceFeignClient,
    private val mileageServiceFeignClient: MileageServiceFeignClient
) : RefundService {

    override fun refundOrder(orderDetailId: Long, refundOrderRequest: RefundOrderRequestDto) {
        // orderDetailId를 사용하여 데이터베이스에서 주문 상세 정보를 조회한다.
        val orderDetail = orderDetailRepository.findById(orderDetailId)
            .orElseThrow { OrderDetailNotFoundException(orderDetailId) }

        // 조회된 주문 상태를 확인하여, 환불이 가능한 상태인지 확인한다.
        if (orderDetail.status != DELIVERY_COMPLETED) {
            throw UnableToRefundException()
        }

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
            retrieverAddress = orderDetail.order.recipient!!.address,
            retrievalMessage = refundOrderRequest.retrievalMessage
        )

        // 환불, 회수 객체를 서로 연관 관계로 설정한다.
        refund.assignRecall(recall)

        // 환불 요청 정보 및 회수 정보를 데이터베이스에 저장한다.
        // CascadeType.ALL 설정으로 인해 Recall 엔티티는 Refund 엔티티와 함께 자동으로 처리된다.
        refundRepository.save(refund)

        // 주문 상세 정보에 환불 정보를 연관 관계 편의 메서드를 사용하여 추가한다.
        orderDetail.assignRefund(refund)

        // 취소된 주문에 포함된 상품의 개수를 상품 서비스에 요청하여 재고를 증가시킨다.
        itemServiceFeignClient.increaseItemStock(orderDetail.itemId, orderDetail.quantity)

        // 주문에 사용된 마일리지를 마일리지 서비스에 요청하여 사용자에게 환불한다.
        val refundMileageInfo = RefundMileageRequestDTO(
            userId = orderDetail.order.userId,
            amount = orderDetail.order.productPaymentAmount,
            reason = "주문 취소"
        )
        mileageServiceFeignClient.refundMileage(orderDetail.order.userId, refundMileageInfo)
    }

    @Transactional(readOnly = true)
    override fun getRefund(refundId: Long): RefundDetailDto {
        // orderId를 사용하여 데이터베이스에서 환불 상세 정보를 조회한다.
        val refund = refundRepository.findById(refundId)
            .orElseThrow { RefundNotFoundException(refundId) }

        // 조회된 환불 상세 정보를 RefundDetailDto로 변환 및 반환한다.
        return RefundDetailDto(
            refundDto = convertRefundEntityToDto(refund),
            recallInfo = RetrievalInfo.from(refund.recall!!),
            refundInfo = RefundInfo( // 추후 정책에 따라 환불 가격 변동이 있을 수 있으므로 다음과 같이 처리
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
    fun convertRefundEntityToDto(refund: Refund): RefundDto {
        // 환불 요청한 주문의 상세 정보와 사용자 정보를 얻습니다.
        val orderDetail = refund.orderDetail
        val order = orderDetail.order

        // ProductInfoDto 생성
        val productInfo = itemServiceFeignClient.getItemInfo(orderDetail.itemId).data // 상품 정보 가져오기
        val productInfoDto = ProductInfoDto.from(productInfo)

        // OrderedProductInfo 생성
        val orderedProductInfo = OrderedProductInfo(
            orderDetailId = orderDetail.id!!,
            orderStatus = orderDetail.status,
            productInfoDto = productInfoDto,
            productQuantity = orderDetail.quantity
        )

        // RefundDto 생성
        return RefundDto(
            refundId = refund.id!!,
            refundRequestDate = refund.refundRequestDate!!.toLocalDate(),
            userId = order.userId,
            orderId = order.id!!,
            orderDate = order.date!!.toLocalDate(),
            product = orderedProductInfo,
            expectedRefundAmount = refund.refundAmount,
            refundStatus = refund.refundStatus
        )
    }

    @Transactional(readOnly = true)
    override fun getRefundList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        reason: Reason?,
        refundId: Long?,
        pageable: Pageable
    ): Page<RefundDto> {

        return refundRepository
            .findRefunds(startDate, endDate, reason, refundId, pageable)
            .map { convertRefundEntityToDto(it) }
    }

    override fun updateRefundStatus(orderDetailId: Long, refundStatusDto: RefundStatusDto) {
        // orderDetailId를 사용하여 데이터베이스에서 주문 상세 정보를 조회한다.
        val orderDetail = orderDetailRepository.findById(orderDetailId)
            .orElseThrow { OrderDetailNotFoundException(orderDetailId) }

        // 주문 상세 정보에서 환불 상세 정보를 조회한다.
        val refund = orderDetail.refund
            ?: throw RefundNotFoundException(orderDetailId)

        // RefundStatusDto에서 변경할 환불 상태를 추출한다.
        val newStatus = refundStatusDto.status

        // 변경할 환불 상태가 현재 환불 상태와 동일한지 확인한다.
        if (refund.refundStatus == newStatus) {
            throw RefundAlreadyInRequestedStatusException()
        }

        // 환불 상태 변경이 유효한지 검증한다.
        validateStatusChange(refund.refundStatus, newStatus)

        // 환불 상태 변경이 유효하다면 환불 상세 정보의 상태를 변경한다.
        refund.refundStatus = newStatus

        // 변경할 환불 상태가 '반품 완료'인 경우, 주문에 사용된 마일리지를 사용자에게 환불하고, 환불된 상품의 재고를 증가시킨다.
        if (newStatus == RefundStatus.REFUND_COMPLETE) {
            // 주문에 사용된 마일리지를 마일리지 서비스에 요청하여 사용자에게 환불한다.
            val refundMileageInfo = RefundMileageRequestDTO(
                userId = orderDetail.order.userId,
                amount = orderDetail.purchasePrice,
                reason = "주문 취소"
            )
            mileageServiceFeignClient.refundMileage(orderDetail.order.userId, refundMileageInfo)

            // 취소된 주문에 포함된 상품의 개수를 상품 서비스에 요청하여 재고를 증가시킨다.
            itemServiceFeignClient.increaseItemStock(refund.orderDetail.itemId, refund.orderDetail.quantity)
        }
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
        if (currentStatus.ordinal + 1 != targetStatus.ordinal) {
            throw InvalidRefundStatusChangeException(currentStatus.name, targetStatus.name)
        }
    }

}
