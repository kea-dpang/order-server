package kea.dpang.order.dto.refund

import jakarta.persistence.Convert
import kea.dpang.order.converter.StringToRefundReasonConverter
import kea.dpang.order.entity.RefundReason

/**
 * 주문 환불 요청 정보를 담는 DTO
 *
 * @property refundReason 환불 사유
 * @property remark 비고
 * @property retrievalMessage 회수 메시지
 */
data class RefundOrderRequestDto(
    @Convert(converter = StringToRefundReasonConverter::class)
    val refundReason: RefundReason, // 환불 사유
    val remark: String?, // 비고
    val retrievalMessage: String? // 회수 메시지
)
