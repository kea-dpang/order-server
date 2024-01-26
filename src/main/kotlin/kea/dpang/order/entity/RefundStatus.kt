package kea.dpang.order.entity

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * 환불 상태를 나타내는 Enum 클래스
 */
enum class RefundStatus {

    /**
     * 반품 요청
     */
    REFUND_REQUEST,

    /**
     * 회수 중
     */
    COLLECTING,

    /**
     * 반품 완료
     */
    REFUND_COMPLETE;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): RefundStatus {
            return RefundStatus.valueOf(value.uppercase())
        }
    }
}