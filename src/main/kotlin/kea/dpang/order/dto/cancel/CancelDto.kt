package kea.dpang.order.dto.cancel

import kea.dpang.order.dto.OrderedProductInfo
import java.time.LocalDate

/**
 * 취소 정보를 담는 DTO
 *
 * @property cancelId 취소 식별자
 * @property cancelRequestDate 취소 신청일
 * @property orderId 주문 식별자
 * @property orderDate 주문 일자
 * @property product 상품 정보
 * @property expectedCancelAmount 환불 예정액
 */
data class CancelDto(
    val cancelId: Long,
    val cancelRequestDate: LocalDate,
    val orderId: Long,
    val orderDate: LocalDate,
    val product: OrderedProductInfo,
    val expectedCancelAmount: Int
)
// Todo: 사용자 식별자