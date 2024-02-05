package kea.dpang.order.feign.dto

data class UpdateStockRequestDto(
    var itemId: Long, // 상품 ID
    var quantity: Int // 변경할 수량
)