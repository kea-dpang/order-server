package kea.dpang.order.dto.refund

import kea.dpang.order.entity.Reason
import kea.dpang.order.entity.Refund

/**
 * 반품 정보를 담는 클래스
 *
 * @property reason 반품 사유
 * @property note 비고
 */
data class ReturnInfo(
    val reason: Reason, // 반품 사유
    val note: String? // 비고
) {
    companion object {
        fun from(refund: Refund): ReturnInfo {
            return ReturnInfo(
                reason = refund.refundReason,
                note = refund.note
            )
        }
    }
}
