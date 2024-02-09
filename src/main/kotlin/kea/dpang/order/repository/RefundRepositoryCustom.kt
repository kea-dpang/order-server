package kea.dpang.order.repository

import kea.dpang.order.entity.Refund
import kea.dpang.order.entity.RefundStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

/**
 * 환불 엔티티에 대한 사용자 정의 리포지토리 인터페이스
 */
fun interface RefundRepositoryCustom {

    /**
     * 주어진 검색 조건과 페이징 정보에 따라 환불 목록을 조회합니다.
     *
     * @param startDate    조회할 환불의 시작 날짜
     * @param endDate      조회할 환불의 종료 날짜
     * @param refundStatus 조회할 환불의 상태
     * @param userId       사용자 식별자
     * @param pageable     페이징 정보를 나타내는 [Pageable] 객체
     * @return [Page] 인터페이스를 구현한 객체를 통해 페이징 처리된 환불 목록
     */
    fun findRefunds(
        startDate: LocalDate?,
        endDate: LocalDate?,
        refundStatus: RefundStatus?,
        userId: Long?,
        pageable: Pageable
    ): Page<Refund>
}