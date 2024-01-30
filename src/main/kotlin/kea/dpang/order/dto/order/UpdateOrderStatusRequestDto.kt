package kea.dpang.order.dto.order

import jakarta.persistence.Convert
import kea.dpang.order.converter.StringToOrderStatusConverter
import kea.dpang.order.entity.OrderStatus

/**
 * 주문 상태 변경 요청에 필요한 정보를 담는 DTO
 *
 * @property status 변경할 상태
 */
data class UpdateOrderStatusRequestDto(
    @Convert(converter = StringToOrderStatusConverter::class)
    val status: OrderStatus
)