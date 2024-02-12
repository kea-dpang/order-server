package kea.dpang.order.dto.order

import com.fasterxml.jackson.annotation.JsonUnwrapped
import kea.dpang.order.entity.Order

/**
 * 주문 상세 정보를 담는 DTO
 *
 * @property order 주문 정보
 * @property deliveryInfo 배송지 정보
 * @property paymentInfo 결제 정보
 * @property deliveryRequest 배송 요청 사항
 */
data class OrderDetailDto(

    @JsonUnwrapped
    val order: OrderDto,
    val deliveryInfo: DeliveryInfo,
    val paymentInfo: PaymentInfo,
    val deliveryRequest: String
) {

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
}