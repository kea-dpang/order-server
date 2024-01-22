package kea.dpang.order.dto.order

/**
 * 주문 요청에 필요한 정보를 담는 DTO
 *
 * @property deliveryInfo 배송지 정보
 * @property productInfo 상품 정보
 */
data class OrderRequestDto(
    val deliveryInfo: DeliveryInfo,
    val productInfo: List<ProductInfo>
)
