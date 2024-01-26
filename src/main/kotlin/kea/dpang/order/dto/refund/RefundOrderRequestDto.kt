package kea.dpang.order.dto.refund

import kea.dpang.order.entity.Reason

/**
 * 주문 환불 요청 정보를 담는 DTO
 *
 * @property refundReason 환불 사유
 * @property remark 비고
 * @property retrievalMessage 회수 메시지
 */
data class RefundOrderRequestDto(
    val refundReason: Reason, // 환불 사유
    val remark: String?, // 비고
    val retrievalMessage: String? // 회수 메시지
)
