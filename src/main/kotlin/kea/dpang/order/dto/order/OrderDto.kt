package kea.dpang.order.dto.order

import kea.dpang.order.dto.OrderedProductInfo
import java.time.LocalDate

/**
 * 주문 정보를 담는 DTO
 *
 * @property orderId 주문 식별자
 * @property orderDate 주문 날짜
 * @property productList 주문 상품 정보 리스트
 * @property orderer 주문자
 */
data class OrderDto(
    val orderId: Long,
    val orderDate: LocalDate,
    val productList: List<OrderedProductInfo>,
    val orderer: String
)
