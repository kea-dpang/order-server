package kea.dpang.order.controller

import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.refund.RefundDetailDto
import kea.dpang.order.dto.refund.RefundDto
import kea.dpang.order.dto.refund.UpdateRefundStatusRequestDto
import kea.dpang.order.entity.RefundStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import java.time.LocalDate

interface RefundController {

    /**
     * 환불 조회
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param role 클라이언트의 역할, API 요청에 포함된 'X-DPANG-CLIENT-ROLE' 헤더 값
     * @param refundId 주문 식별자
     * @return 조회된 환불 정보
     */
    fun getRefund(
        clientId: Long,
        role: String,
        refundId: Long
    ): ResponseEntity<SuccessResponse<RefundDetailDto>>

    /**
     * 환불 조회 - List
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param role 클라이언트의 역할, API 요청에 포함된 'X-DPANG-CLIENT-ROLE' 헤더 값
     * @param startDate 환불 요청 시작 날짜
     * @param endDate 환불 요청 종료 날짜
     * @param refundStatus 환불 상태
     * @param userId 사용자 식별자
     * @param pageable 페이지네이션 정보
     * @return 조회된 환불 목록
     */
    fun getRefundList(
        clientId: Long,
        role: String,
        startDate: LocalDate?,
        endDate: LocalDate?,
        refundStatus: RefundStatus?,
        userId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<RefundDto>>>

    /**
     * 환불 상태 업데이트 - 관리자
     *
     * @param role 클라이언트의 역할, API 요청에 포함된 'X-DPANG-CLIENT-ROLE' 헤더 값
     * @param refundId 환불 식별자
     * @param refundStatusDto 환불 상태 정보
     * @return 업데이트 결과 메시지
     */
    fun updateRefundStatus(
        role: String,
        refundId: Long,
        refundStatusDto: UpdateRefundStatusRequestDto
    ): ResponseEntity<BaseResponse>
}