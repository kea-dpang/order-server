package kea.dpang.order.dto.cancel

import kea.dpang.order.dto.OrderedProductInfo
import kea.dpang.order.dto.ProductInfoDto
import kea.dpang.order.entity.Cancel
import java.time.LocalDate

/**
 * 취소 정보를 담는 DTO
 *
 * @property cancelId 취소 식별자
 * @property userId 사용자 식별자
 * @property userName 사용자 이름
 * @property cancelRequestDate 취소 신청일
 * @property orderId 주문 식별자
 * @property orderDate 주문 일자
 * @property product 상품 정보
 * @property totalAmount 상품 합계 금액
 * @property expectedRefundAmount 환불 예정액
 */
data class CancelDto(
    val cancelId: Long,
    val userId: Long,
    val userName: String,
    val cancelRequestDate: LocalDate,
    val orderId: Long,
    val orderDate: LocalDate,
    val product: OrderedProductInfo,
    val totalAmount: Int,
    val expectedRefundAmount: Int
) {
    constructor(cancel: Cancel, userName: String, productInfoDto: ProductInfoDto) : this(
        cancelId = cancel.id!!,
        userId = cancel.orderDetail.order.userId,
        userName = userName,
        cancelRequestDate = cancel.requestDate!!.toLocalDate(),
        orderId = cancel.orderDetail.order.id!!,
        orderDate = cancel.orderDetail.order.date!!.toLocalDate(),
        product = OrderedProductInfo(
            orderDetailId = cancel.orderDetail.id!!,
            orderStatus = cancel.orderDetail.status,
            productInfoDto = productInfoDto,
            productQuantity = cancel.orderDetail.quantity
        ),
        totalAmount = productInfoDto.price * cancel.orderDetail.quantity,
        expectedRefundAmount = cancel.refundAmount
    )
}