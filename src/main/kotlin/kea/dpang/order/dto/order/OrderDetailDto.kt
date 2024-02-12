package kea.dpang.order.dto.order

import com.fasterxml.jackson.annotation.JsonUnwrapped

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
)