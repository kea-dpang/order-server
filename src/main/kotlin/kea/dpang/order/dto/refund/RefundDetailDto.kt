package kea.dpang.order.dto.refund

import com.fasterxml.jackson.annotation.JsonUnwrapped
import kea.dpang.order.entity.Refund
import kea.dpang.order.entity.RefundReason
import kea.dpang.order.entity.RefundStatus

/**
 * 환불 조회 결과를 담는 DTO
 *
 * @property refundDto 환불 정보
 * @property recallInfo 회수처 정보
 * @property refundInfo 환불 정보
 * @property returnInfo 반품 정보
 */
data class RefundDetailDto(

    @JsonUnwrapped
    val refundDto: RefundDto, // 환불 정보
    val recallInfo: RetrievalInfo, // 회수처 정보
    val refundInfo: RefundInfo, // 환불 정보
    val returnInfo: ReturnInfo // 반품 정보
) {

    /**
     * 환불 정보를 담는 클래스
     *
     * @property productPaymentAmount 상품 결재 금액.
     * @property expectedRefundAmount 환불 예정액.
     * @property refundStatus 환불 상태.
     */
    data class RefundInfo(
        val productPaymentAmount: Int,
        val expectedRefundAmount: Int,
        val refundStatus: RefundStatus
    )

    /**
     * 반품 정보를 담는 클래스
     *
     * @property refundReason 반품 사유
     * @property note 비고
     */
    data class ReturnInfo(
        val refundReason: RefundReason, // 반품 사유
        val note: String? // 비고
    ) {
        companion object {
            fun from(refund: Refund): ReturnInfo {
                return ReturnInfo(
                    refundReason = refund.refundReason,
                    note = refund.note
                )
            }
        }
    }
}
