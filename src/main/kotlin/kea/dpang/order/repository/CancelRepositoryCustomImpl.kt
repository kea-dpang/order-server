package kea.dpang.order.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import kea.dpang.order.entity.Cancel
import kea.dpang.order.entity.QCancel
import kea.dpang.order.entity.QOrderDetail
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
    QCancel.cancel.id,
    QOrderDetail.orderDetail.order.userId
), CancelRepositoryCustom {

    override fun findCancels(
        startDate: LocalDate?,
        endDate: LocalDate?,
        userId: Long?, // 사용자 ID를 검색 조건으로 추가
        pageable: Pageable
    ): Page<Cancel> {
        val builder = BooleanBuilder()
        return findEntities(startDate, endDate, userId, pageable, builder)
    }
}
