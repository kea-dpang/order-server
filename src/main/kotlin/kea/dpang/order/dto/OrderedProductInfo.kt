package kea.dpang.order.dto

import kea.dpang.order.feign.dto.ProductInfo

/**
 * 주문 상품 정보를 담는 DTO
 *
 * @property orderDetailId 주문 상세 ID
 * @property productInfo 상품 정보
 * @property productQuantity 상품 수량
 */
data class OrderedProductInfo(
    val orderDetailId: String,
    val productInfo: ProductInfo,
    val productQuantity: Int
)