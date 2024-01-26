package kea.dpang.order.dto.order

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