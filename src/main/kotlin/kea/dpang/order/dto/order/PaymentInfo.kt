package kea.dpang.order.dto.order

/**
 * 결제 정보를 담는 DTO
 *
 * @property productTotalPrice 상품 합계 금액
 * @property deliveryFee 배송비
 * @property paymentStatus 결제 상태
 */
data class PaymentInfo(
    val productTotalPrice: Int,
    val deliveryFee: Int,
    val paymentStatus: Boolean
)