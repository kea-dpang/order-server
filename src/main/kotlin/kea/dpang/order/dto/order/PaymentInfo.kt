package kea.dpang.order.dto.order

import kea.dpang.order.entity.Order

/**
 * 결제 정보를 담는 DTO
 *
 * @property productTotalPrice 상품 합계 금액
 * @property deliveryFee 배송비
 */
data class PaymentInfo(
    val productTotalPrice: Int,
    val deliveryFee: Int,
) {
    companion object {
        fun from(order: Order): PaymentInfo {
            return PaymentInfo(
                productTotalPrice = order.productPaymentAmount,
                deliveryFee = order.deliveryFee
            )
        }
    }
}