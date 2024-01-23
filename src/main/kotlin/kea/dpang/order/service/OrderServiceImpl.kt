package kea.dpang.order.service

import kea.dpang.order.dto.order.OrderDetailDto
import kea.dpang.order.dto.order.OrderDto
import kea.dpang.order.dto.order.OrderRequestDto
import kea.dpang.order.dto.order.UpdateOrderStatusRequestDto
import kea.dpang.order.entity.OrderStatus
import kea.dpang.order.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class OrderServiceImpl(private val orderRepository: OrderRepository) : OrderService {

    override fun placeOrder(orderRequest: OrderRequestDto): OrderDto {
        TODO("Not yet implemented")
        /*
            1. OrderRequestDto에서 주문 정보를 추출한다.
            2. 주문 상품의 재고가 충분한지 확인한다. 주문 상품의 재고가 충분하지 않다면 주문을 처리하지 않고 예외를 발생시킨다.
            3. 사용자의 마일리지가 주문에 사용할 마일리지보다 많은지 확인한다. 만약 사용자의 마일리지가 부족하다면 주문을 처리하지 않고 예외를 발생시킨다.
            4. 주문 정보를 데이터베이스에 저장한다. 이때, 주문 상태는 '주문 접수'로 설정한다.
            5. 주문이 성공적으로 처리되면, 주문 상품의 재고를 감소시키고, 사용자의 마일리지를 감소시킨다.
            6. 주문 상태를 '결제 완료'로 변경한다.
            7. 마지막으로, 저장된 주문 정보를 반환한다.
         */
    }

    override fun updateOrderStatus(orderId: String, updateOrderStatusRequest: UpdateOrderStatusRequestDto) {
        TODO("Not yet implemented")
        /*
            1. orderId를 사용하여 데이터베이스에서 주문 정보를 조회한다. 이때, 주문 정보가 없으면 예외를 발생킨다.
            2. UpdateOrderStatusRequestDto에서 변경할 주문 상태를 추출한다.
            3. 변경할 주문 상태가 현재 주문 상태와 동일한지 확인한다. 만약 동일하다면 상태 변경을 진행하지 않고 메소드를 종료한다.
            4. 주문 상태 변경이 유효한지 검증한다. 예를 들어, 주문 상태를 '배송 완료'로 변경하려면 현재 주문 상태가 '배송중'이어야 한다. 이와 같이 주문 상태 변경은 특정 상태에서만 가능해야 한다.
            5. 주문 상태 변경이 유효하다면 주문 정보의 상태를 변경한다.
            6. 변경된 주문 정보를 데이터베이스에 업데이트한다.
         */
    }

    override fun getOrderList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        orderStatus: OrderStatus?,
        orderId: String?,
        pageable: Pageable
    ): Page<OrderDto> {
        TODO("Not yet implemented")
        /*
            1. startDate, endDate, orderStatus, orderId와 같은 필터링 조건을 사용하여 데이터베이스에서 주문 정보 목록을 조회한다. 이때, pageable 객체를 사용하여 페이징 처리를 적용한다.
            2. 조회된 주문 정보 목록을 OrderDto로 변환한다. 각 환불 정보를 OrderDto로 변환하는 로직을 목록의 각 요소에 적용한다.
            3. 변환된 OrderDto 목록을 Page 객체로 래핑하여 반환한다. 이때, 원본 Page 객체의 메타데이터(예: 총 페이지 수, 현재 페이지 번호 등)를 유지해야 한다.
         */
    }

    override fun getOrder(orderId: String): OrderDetailDto {
        TODO("Not yet implemented")
        /*
            1. orderId를 사용하여 데이터베이스에서 주문 상세 정보를 조회한다. 이때, 주문 상세 정보가 없으면 예외를 발생시킨다.
            2. 조회된 주문 상세 정보를 변환 및 반환한다.
         */
    }
}
