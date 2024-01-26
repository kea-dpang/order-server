package kea.dpang.order.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import kea.dpang.order.entity.Order
import kea.dpang.order.entity.QOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class OrderRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : OrderRepositoryCustom {

    override fun findOrders(
        startDate: LocalDate?,
        endDate: LocalDate?,
        orderId: Long?,
        pageable: Pageable
    ): Page<Order> {

        val order = QOrder.order
        val builder = BooleanBuilder()

        // 주어진 조건에 따라 동적으로 where 절을 구성한다.
        startDate?.let { builder.and(order.date.goe(it.atStartOfDay())) }
        endDate?.let { builder.and(order.date.before(it.plusDays(1).atStartOfDay())) }
        orderId?.let { builder.and(order.id.eq(it)) }

        // QueryDSL의 JPAQuery를 사용하여 동적 쿼리를 생성하고 실행한다.
        val orders = jpaQueryFactory
            .selectFrom(order) // Order 엔티티를 대상으로 쿼리를 수행한다.
            .where(builder) // where 절에 동적으로 생성한 조건을 적용한다.
            .offset(pageable.offset) // 페이지의 시작 위치를 설정한다.
            .limit(pageable.pageSize.toLong()) // 페이지 크기를 설정한다.
            .fetch() // 쿼리를 실행하고, 결과를 리스트로 반환한다.

        return PageImpl(orders, pageable, orders.size.toLong())
    }

}