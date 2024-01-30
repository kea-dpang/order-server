package kea.dpang.order.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import kea.dpang.order.entity.Order
import kea.dpang.order.entity.QOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class OrderRepositoryCustomImpl(
    jpaQueryFactory: JPAQueryFactory
) : BaseRepositoryCustomImpl<Order>(
    jpaQueryFactory,
    QOrder.order,
    QOrder.order.date,
    QOrder.order.id
), OrderRepositoryCustom {

    override fun findOrders(
        startDate: LocalDate?,
        endDate: LocalDate?,
        orderId: Long?,
        pageable: Pageable
    ): Page<Order> {
        val builder = BooleanBuilder()
        return findEntities(startDate, endDate, orderId, pageable, builder)
    }
}