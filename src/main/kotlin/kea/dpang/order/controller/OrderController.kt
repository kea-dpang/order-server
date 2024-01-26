package kea.dpang.order.controller

import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.order.OrderDetailDto
import kea.dpang.order.dto.order.OrderDto
import kea.dpang.order.dto.order.OrderRequestDto
import kea.dpang.order.dto.order.UpdateOrderStatusRequestDto
import kea.dpang.order.entity.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import java.time.LocalDate

/**
 * 주문 관련 컨트롤러
 */
interface OrderController {

    /**
     * 상품 주문 - 사용자
     *
     * @param userId 사용자 식별자
     * @param orderRequest 배송 요청 정보
     * @return 주문 결과
     */
    fun placeOrder(userId: Long, orderRequest: OrderRequestDto): ResponseEntity<SuccessResponse<OrderDto>>

    /**
     * 주문 상태 수정 - 관리자
     *
     * @param orderId 주문 식별자
     * @param updateOrderStatusRequest 상태 변경 요청 정보
     * @return 변경 결과
     */
    fun updateOrderStatus(
        orderId: Long,
        updateOrderStatusRequest: UpdateOrderStatusRequestDto
    ): ResponseEntity<BaseResponse>

    /**
     * 주문 및 배송 조회 - List
     *
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param orderStatus 조회할 주문 상태
     * @param orderId 조회할 주문 번호
     * @param pageable 페이지네이션 정보
     * @return 조회된 주문 및 배송 목록
     */
    fun getOrderList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        orderStatus: OrderStatus?,
        orderId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<OrderDto>>>

    /**
     * 주문 및 배송 상세 조회
     *
     * @param orderId 조회할 주문의 식별자
     * @return 조회된 주문 및 배송 상세 정보
     */
    fun getOrder(orderId: Long): ResponseEntity<SuccessResponse<OrderDetailDto>>

}
