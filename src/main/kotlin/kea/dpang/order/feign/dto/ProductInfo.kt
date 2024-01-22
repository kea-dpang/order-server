package kea.dpang.order.feign.dto

/**
 * 상품 정보를 담는 DTO
 *
 * @property productImage 상품 이미지
 * @property productName 상품명
 * @property productPrice 상품 금액
 */

data class ProductInfo(
    val productImage: String,
    val productName: String,
    val productPrice: Int
)