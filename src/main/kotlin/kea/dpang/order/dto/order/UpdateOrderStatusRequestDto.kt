package kea.dpang.order.dto.order

import kea.dpang.order.entity.OrderStatus

/**
 * 주문 상태 변경 요청에 필요한 정보를 담는 DTO
 *
 * @property newStatus 변경할 상태
 */
data class UpdateOrderStatusRequestDto(
    val newStatus: OrderStatus
)