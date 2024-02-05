package kea.dpang.order.feign.dto

data class UpdateStockListRequestDto(
    var stockUpdateRequests: List<UpdateStockRequestDto>
)