package kea.dpang.order.dto.refund

import kea.dpang.order.entity.RefundStatus

/**
 * 환불 상태 정보를 담는 DTO
 *
 * @property status 환불 상태
 */
data class RefundStatusDto(
    val status: RefundStatus
)
