package kea.dpang.order.dto.order

/**
 * 주문 상품 정보를 담는 DTO
 *
 * @property productId 상품 식별자
 * @property productQuantity 상품 수량
 */
data class ProductInfo(
    val productId: String,
    val productQuantity: Int
)