package kea.dpang.order.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import kea.dpang.order.entity.QOrderDetail
import kea.dpang.order.entity.QRefund
import kea.dpang.order.entity.RefundReason
import kea.dpang.order.entity.Refund
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class RefundRepositoryCustomImpl(
    jpaQueryFactory: JPAQueryFactory
) : BaseRepositoryCustomImpl<Refund>(
    jpaQueryFactory,
    QRefund.refund,
    QRefund.refund.refundRequestDate,
    QRefund.refund.id,
    QOrderDetail.orderDetail.order.userId
), RefundRepositoryCustom {

    override fun findRefunds(
        startDate: LocalDate?,
        endDate: LocalDate?,
        refundReason: RefundReason?,
        userId: Long?,
        pageable: Pageable
    ): Page<Refund> {
        val builder = BooleanBuilder()
        refundReason?.let { builder.and(QRefund.refund.refundReason.eq(it)) }
        return findEntities(startDate, endDate, userId, pageable, builder)
    }
}
