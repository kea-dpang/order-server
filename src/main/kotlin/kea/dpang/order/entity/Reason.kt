package kea.dpang.order.entity

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * 환불 사유를 나타내는 Enum 클래스
 */
enum class Reason {

    /**
     * 사이즈가 맞지 않음
     */
    SIZE_NOT_MATCH,

    /**
     * 단순 변심
     */
    SIMPLE_CHANGE,

    /**
     * 제품에 대한 불만족
     */
    PRODUCT_DISCONTENT,

    /**
     * 배송이 지연됨
     */
    DELIVERY_DELAY,

    /**
     * 오배송
     */
    WRONG_DELIVERY,

    /**
     * 기타 사유
     */
    OTHERS;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): Reason {
            return Reason.valueOf(value.uppercase())
        }
    }
}