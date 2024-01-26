package kea.dpang.order.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import kea.dpang.order.entity.QRefund
import kea.dpang.order.entity.Reason
import kea.dpang.order.entity.Refund
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class RefundRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : RefundRepositoryCustom {

    override fun findRefunds(
        startDate: LocalDate?,
        endDate: LocalDate?,
        reason: Reason?,
        refundId: Long?,
        pageable: Pageable
    ): Page<Refund> {

        val refund = QRefund.refund
        val builder = BooleanBuilder()

        // 주어진 조건에 따라 동적으로 where 절을 구성한다.
        startDate?.let { builder.and(refund.refundRequestDate.goe(it.atStartOfDay())) }
        endDate?.let { builder.and(refund.refundRequestDate.before(it.plusDays(1).atStartOfDay())) }
        reason?.let { builder.and(refund.refundReason.eq(it)) }
        refundId?.let { builder.and(refund.id.eq(it)) }

        // 페이징 적용
        // QueryDSL의 JPAQuery를 사용하여 동적 쿼리를 생성하고 실행한다.
        val refunds = jpaQueryFactory
            .selectFrom(refund) // Refund 엔티티를 대상으로 쿼리를 수행한다.
            .where(builder) // where 절에 동적으로 생성한 조건을 적용한다.
            .offset(pageable.offset) // 페이지의 시작 위치를 설정한다.
            .limit(pageable.pageSize.toLong()) // 페이지 크기를 설정한다.
            .fetch() // 쿼리를 실행하고, 결과를 리스트로 반환한다.

        return PageImpl(refunds, pageable, refunds.size.toLong())
    }
}