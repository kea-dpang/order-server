package kea.dpang.order.dto.refund

import kea.dpang.order.entity.Reason

/**
 * 반품 정보를 담는 클래스
 *
 * @property reason 반품 사유
 * @property remark 비고
 */
data class ReturnInfo(
    val reason: Reason, // 반품 사유
    val remark: String? // 비고
)
