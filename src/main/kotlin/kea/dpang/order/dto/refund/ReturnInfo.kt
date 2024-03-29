package kea.dpang.order.dto.refund

import kea.dpang.order.entity.Refund
import kea.dpang.order.entity.RefundReason

/**
 * 반품 정보를 담는 클래스
 *
 * @property refundReason 반품 사유
 * @property note 비고
 */
data class ReturnInfo(
    val refundReason: RefundReason, // 반품 사유
    val note: String? // 비고
) {
    companion object {
        fun from(refund: Refund): ReturnInfo {
            return ReturnInfo(
                refundReason = refund.refundReason,
                note = refund.note
            )
        }
    }
}