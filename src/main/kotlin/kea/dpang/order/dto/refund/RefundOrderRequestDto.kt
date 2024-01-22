package kea.dpang.order.dto.refund

import kea.dpang.order.entity.Reason

/**
 * 주문 환불 요청 정보를 담는 DTO
 *
 * @property refundReason 환불 사유
 * @property remark 비고
 */
data class RefundOrderRequestDto(
    val refundReason: Reason,
    val remark: String?
)
