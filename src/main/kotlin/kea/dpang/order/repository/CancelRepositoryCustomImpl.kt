package kea.dpang.order.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import kea.dpang.order.entity.Cancel
import kea.dpang.order.entity.QCancel
import kea.dpang.order.entity.Reason
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CancelRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : CancelRepositoryCustom {

    override fun findCancels(
        startDate: LocalDate?,
        endDate: LocalDate?,
        reason: Reason?,
        cancelId: Long?,
        pageable: Pageable
    ): Page<Cancel> {

        val cancel = QCancel.cancel
        val builder = BooleanBuilder()

        // 필터링 조건 추가
        // 주어진 조건에 따라 동적으로 where 절을 구성한다.
        startDate?.let { builder.and(cancel.requestDate.after(it.atStartOfDay())) }
        endDate?.let { builder.and(cancel.requestDate.before(it.plusDays(1).atStartOfDay())) }
        reason?.let { builder.and(cancel.reason.eq(it)) }
        cancelId?.let { builder.and(cancel.id.eq(it)) }

        // 쿼리 생성 및 실행
        val cancelList = jpaQueryFactory
            .selectFrom(cancel)
            .where(builder)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // 변환된 Cancel 목록을 Page 객체로 래핑하여 반환
        return PageImpl(cancelList, pageable, cancelList.size.toLong())
    }

}