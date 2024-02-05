package kea.dpang.order.repository

import kea.dpang.order.entity.Cancel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

/**
 * 취소 엔티티에 대한 사용자 정의 리포지토리 인터페이스
 */
fun interface CancelRepositoryCustom {

    /**
     * 주어진 검색 조건과 페이징 정보에 따라 취소 목록을 조회합니다.
     *
     * @param startDate     조회할 취소의 시작 날짜
     * @param endDate       조회할 취소의 종료 날짜
     * @param userId        사용자 식별자
     * @param pageable      페이징 정보를 나타내는 [Pageable] 객체
     * @return [Page] 인터페이스를 구현한 객체를 통해 페이징 처리된 취소 목록
     */
    fun findCancels(
        startDate: LocalDate?,
        endDate: LocalDate?,
        userId: Long?,
        pageable: Pageable
    ): Page<Cancel>
}
