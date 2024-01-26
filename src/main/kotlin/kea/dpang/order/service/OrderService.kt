package kea.dpang.order.service

import kea.dpang.order.dto.order.OrderDetailDto
import kea.dpang.order.dto.order.OrderDto
import kea.dpang.order.dto.order.OrderRequestDto
import kea.dpang.order.dto.order.UpdateOrderStatusRequestDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface OrderService {

    /**
     * 상품 주문 - 사용자
     *
     * @param orderRequest 주문 요청 정보
     * @return 주문 결과
     */
    fun placeOrder(userId: Long, orderRequest: OrderRequestDto): OrderDto

    /**
     * 주문 상태 수정 - 관리자
     *
     * @param orderDetailId 주문 식별자
     * @param updateOrderStatusRequest 상태 변경 요청 정보
     */
    fun updateOrderStatus(
        orderDetailId: Long,
        updateOrderStatusRequest: UpdateOrderStatusRequestDto
    )

    /**
     * 주문 및 배송 조회 - List
     *
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param orderId 조회할 주문 번호
     * @param pageable 페이지네이션 정보
     * @return 조회된 주문 및 배송 목록
     */
    fun getOrderList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        orderId: Long?,
        pageable: Pageable
    ): Page<OrderDto>

    /**
     * 주문 및 배송 상세 조회
     *
     * @param orderId 조회할 주문의 식별자
     * @return 조회된 주문 및 배송 상세 정보
     */
    fun getOrder(orderId: Long): OrderDetailDto

}
