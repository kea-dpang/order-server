package kea.dpang.order.repository

import kea.dpang.order.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

/**
 * 주문 엔티티에 대한 사용자 정의 리포지토리 인터페이스
 */
interface OrderRepositoryCustom {

    /**
     * 주어진 검색 조건과 페이징 정보에 따라 주문 목록을 조회합니다.
     *
     * @param startDate   조회할 주문의 시작 날짜
     * @param endDate     조회할 주문의 종료 날짜
     * @param orderId     조회할 주문의 식별자
     * @param pageable    페이징 정보를 나타내는 [Pageable] 객체
     * @return [Page] 인터페이스를 구현한 객체를 통해 페이징 처리된 주문 목록
     */
    fun findOrders(
        startDate: LocalDate?,
        endDate: LocalDate?,
        orderId: Long?,
        pageable: Pageable
    ): Page<Order>
}
