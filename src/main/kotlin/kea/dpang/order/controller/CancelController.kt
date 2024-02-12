package kea.dpang.order.controller

import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.cancel.CancelDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import java.time.LocalDate

interface CancelController {

    /**
     * 주문 취소 상세 조회
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param role 클라이언트의 역할, API 요청에 포함된 'X-DPANG-CLIENT-ROLE' 헤더 값
     * @param cancelId 조회할 취소의 식별자
     * @return 조회된 주문 취소 상세 정보
     */
    fun getCancel(
        clientId: Long,
        role: String,
        cancelId: Long
    ): ResponseEntity<SuccessResponse<CancelDto>>

    /**
     * 주문 취소 조회 - List
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param role 클라이언트의 역할, API 요청에 포함된 'X-DPANG-CLIENT-ROLE' 헤더 값
     * @param startDate 취소 요청 시작 날짜
     * @param endDate 취소 요청 종료 날짜
     * @param userId 사용자 식별자
     * @param pageable 페이지네이션 정보
     * @return 조회된 주문 취소 목록
     */
    fun getCancelList(
        clientId: Long,
        role: String,
        startDate: LocalDate?,
        endDate: LocalDate?,
        userId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<CancelDto>>>

}