package kea.dpang.order.controller

import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.cancel.CancelDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import java.time.LocalDate

interface CancelController {

    /**
     * 주문 취소 요청 - 사용자
     *
     * @param orderDetailId 주문 상세 ID
     * @return 취소 결과
     */
    fun cancelOrder(orderDetailId: Long): ResponseEntity<BaseResponse>

    /**
     * 주문 취소 상세 조회
     *
     * @param cancelId 조회할 취소의 식별자
     * @return 조회된 주문 취소 상세 정보
     */
    fun getCancel(cancelId: Long): ResponseEntity<SuccessResponse<CancelDto>>

    /**
     * 주문 취소 조회 - List
     *
     * @param startDate 취소 요청 시작 날짜
     * @param endDate 취소 요청 종료 날짜
     * @param userId 사용자 식별자
     * @param pageable 페이지네이션 정보
     * @return 조회된 주문 취소 목록
     */
    fun getCancelList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        userId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<CancelDto>>>

}