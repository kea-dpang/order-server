package kea.dpang.order.feign.dto

/**
 * 상품 정보를 나타내는 DTO
 *
 * @property id 상품의 고유 식별자 ID
 * @property name 상품명.
 * @property price 상품의 정가
 * @property quantity 상품의 재고 수량
 * @property discountRate 상품의 할인율(% 단위)
 * @property discountPrice 할인 적용 후의 상품 가격
 * @property thumbnailImage 상품 이미지의 URL 또는 경로
 */
data class ItemInfoDto(
    val id: Long, // 아이템 ID
    val name: String, // 상품명
    val price: Int, // 상품 가격
    val quantity: Int, // 상품 재고 수량
    val discountRate: Int, // 상품 할인율
    val discountPrice: Int, // 상품 할인가
    val thumbnailImage: String // 상품 이미지
)
