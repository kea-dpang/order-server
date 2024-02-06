package kea.dpang.order.dto.refund

import kea.dpang.order.dto.OrderedProductInfo
import kea.dpang.order.entity.Reason
import kea.dpang.order.entity.RefundStatus
import java.time.LocalDate

/**
 * 환불 요약 정보를 담는 DTO
 *
 * @property refundId 환불 식별자
 * @property refundRequestDate 환불 신청일
 * @property userId 사용자 식별자
 * @property orderId 주문 식별자
 * @property orderDate 주문 일자
 * @property product 상품 정보
 * @property expectedRefundAmount 환불 예정액
 * @property refundStatus 상태
 * @property refundReason 환불 사유
 */
data class RefundDto(
    val refundId: Long,
    val refundRequestDate: LocalDate,
    val userId: Long,
    val orderId: Long,
    val orderDate: LocalDate,
    val product: OrderedProductInfo,
    val expectedRefundAmount: Int,
    val refundStatus: RefundStatus,
    val refundReason: Reason
)
