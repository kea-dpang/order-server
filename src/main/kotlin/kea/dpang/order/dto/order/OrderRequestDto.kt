package kea.dpang.order.dto.order

/**
 * 주문 요청에 필요한 정보를 담는 DTO
 *
 * @property deliveryInfo 배송지 정보
 * @property deliveryRequest 배송 요청사항
 * @property orderItemInfo 상품 정보
 */
data class OrderRequestDto(
    val deliveryInfo: DeliveryInfo,
    var deliveryRequest: String,
    val orderItemInfo: List<ItemInfo>
) {

    /**
     * 주문 상품 정보를 담는 DTO
     *
     * @property itemId 상품 식별자
     * @property quantity 상품 수량
     */
    data class ItemInfo(
        val itemId: Long,
        val quantity: Int
    )
}
