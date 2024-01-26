package kea.dpang.order.dto

import kea.dpang.order.entity.OrderStatus

/**
 * 주문 상품 정보를 담는 DTO
 *
 * @property orderDetailId 주문 상세 ID
 * @property orderStatus 주문 상태
 * @property productInfoDto 상품 정보
 * @property productQuantity 상품 수량
 */
data class OrderedProductInfo(
    val orderDetailId: Long,
    val orderStatus: OrderStatus,
    val productInfoDto: ProductInfoDto,
    val productQuantity: Int
)