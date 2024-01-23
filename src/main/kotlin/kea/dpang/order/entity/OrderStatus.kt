package kea.dpang.order.entity

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * 주문 상태를 표현하는 Enum 클래스
 */
enum class OrderStatus {

    /**
     * 주문 접수 상태
     */
    ORDER_RECEIVED,

    /**
     * 결제 완료 상태
     */
    PAYMENT_COMPLETED,

    /**
     * 배송 요청 상태
     */
    DELIVERY_REQUESTED,

    /**
     * 배송 준비중 상태
     */
    PREPARING_FOR_DELIVERY,

    /**
     * 배송중 상태
     */
    IN_DELIVERY,

    /**
     * 배송 완료 상태
     */
    DELIVERY_COMPLETED;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): OrderStatus {
            return OrderStatus.valueOf(value.uppercase())
        }
    }
}
