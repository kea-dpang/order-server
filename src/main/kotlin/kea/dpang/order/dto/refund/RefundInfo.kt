package kea.dpang.order.dto.refund

import kea.dpang.order.entity.RefundStatus

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