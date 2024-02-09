package kea.dpang.order.controller

import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.refund.RefundDetailDto
import kea.dpang.order.dto.refund.RefundDto
import kea.dpang.order.dto.refund.RefundStatusDto
import kea.dpang.order.entity.RefundStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import java.time.LocalDate

interface RefundController {

    /**
     * 환불 조회
     *
     * @param refundId 주문 식별자
     * @return 조회된 환불 정보
     */
    fun getRefund(refundId: Long): ResponseEntity<SuccessResponse<RefundDetailDto>>

    /**
     * 환불 조회 - List
     *
     * @param startDate 환불 요청 시작 날짜
     * @param endDate 환불 요청 종료 날짜
     * @param refundStatus 환불 상태
     * @param userId 사용자 식별자
     * @param pageable 페이지네이션 정보
     * @return 조회된 환불 목록
     */
    fun getRefundList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        refundStatus: RefundStatus?,
        userId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<RefundDto>>>

    /**
     * 환불 상태 업데이트 - 관리자
     *
     * @param refundId 환불 식별자
     * @param refundStatusDto 환불 상태 정보
     * @return 업데이트 결과 메시지
     */
    fun updateRefundStatus(refundId: Long, refundStatusDto: RefundStatusDto): ResponseEntity<BaseResponse>
}