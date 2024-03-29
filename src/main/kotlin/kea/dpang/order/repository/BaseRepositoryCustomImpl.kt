package kea.dpang.order.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.dsl.DateTimePath
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 공통적인 동적 쿼리 생성 및 실행 로직을 제공하는 베이스 리포지토리 클래스입니다.
 * 이 클래스를 상속받은 자식 클래스는 findEntities 메서드를 호출하여 동적 쿼리를 생성하고 실행할 수 있습니다.
 *
 * @param <T> 엔티티 타입
 * @param jpaQueryFactory JPAQueryFactory 인스턴스
 * @param entityPath 쿼리 대상 엔티티의 Q 클래스 인스턴스
 * @param datePath 쿼리에서 사용할 날짜 필드의 Q 클래스 인스턴스
 * @param idPath 쿼리에서 사용할 ID 필드의 Q 클래스 인스턴스
 * @param userPath 쿼리에서 사용할 사용자 ID 필드의 Q 클래스 인스턴스
 */
abstract class BaseRepositoryCustomImpl<T>(
    private val jpaQueryFactory: JPAQueryFactory,
    private val entityPath: EntityPath<T>,
    private val datePath: DateTimePath<LocalDateTime>,
    private val idPath: NumberPath<Long>,
    private val userPath: NumberPath<Long>
) {
    /**
     * 주어진 조건에 따라 동적 쿼리를 생성하고 실행하여 결과를 페이지네이션된 형태로 반환합니다.
     * 이 메서드는 날짜와 ID 필드에 대한 검색을 수행하며, 추가적인 필터링 조건을 builder 파라미터로 받아 처리합니다.
     *
     * @param startDate 쿼리 시작 날짜
     * @param endDate 쿼리 종료 날짜
     * @param userId 사용자 식별자
     * @param pageable 페이지네이션 정보
     * @param builder 추가적인 필터링 조건을 담은 BooleanBuilder
     * @return 쿼리 결과를 담은 Page 객체
     */
    protected fun findEntities(
        startDate: LocalDate?,
        endDate: LocalDate?,
        userId: Long?,
        pageable: Pageable,
        builder: BooleanBuilder
    ): Page<T> {
        // 날짜와 ID 필드에 대한 검색 조건을 추가한다.
        startDate?.let { builder.and(datePath.goe(it.atStartOfDay())) }
        endDate?.let { builder.and(datePath.before(it.plusDays(1).atStartOfDay())) }
        userId?.let { builder.and(userPath.eq(it)) }

        // 쿼리를 실행하여 결과를 페이지네이션된 형태로 반환한다.
        val entityList = fetchEntities(pageable, builder)

        // 전체 엔티티의 개수를 가져온다.
        val totalEntitiesCount = fetchTotalEntitiesCount(builder)

        return PageImpl(entityList, pageable, totalEntitiesCount)
    }

    /**
     * 주어진 필터링 조건에 따라 쿼리를 실행하여 결과를 가져옵니다.
     *
     * @param pageable 페이지네이션 정보
     * @param builder 필터링 조건을 담은 BooleanBuilder
     * @return 쿼리 결과를 담은 List 객체
     */
    private fun fetchEntities(
        pageable: Pageable,
        builder: BooleanBuilder
    ): List<T> {
        return jpaQueryFactory
            .selectFrom(entityPath)
            .where(builder)
            .orderBy(datePath.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    /**
     * 주어진 필터링 조건에 따라 쿼리를 실행하여 전체 엔티티의 개수를 가져옵니다.
     *
     * @param builder 필터링 조건을 담은 BooleanBuilder
     * @return 전체 엔티티의 개수
     */
    private fun fetchTotalEntitiesCount(builder: BooleanBuilder): Long {
        return jpaQueryFactory
            .selectFrom(entityPath)
            .where(builder)
            .fetch()
            .size
            .toLong()
    }
}
