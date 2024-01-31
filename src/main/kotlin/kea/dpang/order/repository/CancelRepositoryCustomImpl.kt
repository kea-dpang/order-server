package kea.dpang.order.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import kea.dpang.order.entity.Cancel
import kea.dpang.order.entity.QCancel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CancelRepositoryCustomImpl(
    jpaQueryFactory: JPAQueryFactory
) : BaseRepositoryCustomImpl<Cancel>(
    jpaQueryFactory,
    QCancel.cancel,
    QCancel.cancel.requestDate,
    QCancel.cancel.id
), CancelRepositoryCustom {

    override fun findCancels(
        startDate: LocalDate?,
        endDate: LocalDate?,
        cancelId: Long?,
        pageable: Pageable
    ): Page<Cancel> {
        val builder = BooleanBuilder()
        return findEntities(startDate, endDate, cancelId, pageable, builder)
    }
}