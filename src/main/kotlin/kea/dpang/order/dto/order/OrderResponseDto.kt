package kea.dpang.order.dto.order

import com.fasterxml.jackson.annotation.JsonUnwrapped
import kea.dpang.order.feign.dto.MileageDto

data class OrderResponseDto(

    @JsonUnwrapped
    val order: OrderDto,
    val mileageInfo: MileageInfo
) {

    companion object {
        fun from(order: OrderDto, mileageDto: MileageDto): OrderResponseDto {
            return OrderResponseDto(order, MileageInfo.from(mileageDto))
        }
    }
}