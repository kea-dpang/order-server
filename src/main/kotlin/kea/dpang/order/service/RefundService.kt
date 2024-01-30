package kea.dpang.order.service

import kea.dpang.order.dto.refund.RefundDetailDto
import kea.dpang.order.dto.refund.RefundDto
import kea.dpang.order.dto.refund.RefundOrderRequestDto
import kea.dpang.order.dto.refund.RefundStatusDto
import kea.dpang.order.entity.Reason
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface RefundService {

    /**
     * 주문 환불 요청 - 사용자
     *
     * @param orderDetailId 주문 상세 ID
     * @param refundOrderRequest 환불 요청 정보
     * @return 환불 결과
     */
    fun refundOrder(orderDetailId: Long, refundOrderRequest: RefundOrderRequestDto)

    /**
     * 환불 조회
     *
     * @param refundId 환불 식별자
     * @return 조회된 환불 정보
     */
    fun getRefund(refundId: Long): RefundDetailDto

    /**
     * 환불 조회 - List
     *
     * @param startDate 환불 요청 시작 날짜
     * @param endDate 환불 요청 종료 날짜
     * @param reason 환불 사유
     * @param refundId 조회할 환불 번호
     * @param pageable 페이지네이션 정보
     * @return 조회된 환불 목록
     */
    fun getRefundList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        reason: Reason?,
        refundId: Long?,
        pageable: Pageable
    ): Page<RefundDto>

    /**
     * 환불 상태 업데이트 - 관리자
     *
     * @param orderDetailId 주문 상세 ID
     * @param refundStatusDto 환불 상태 정보
     */
    fun updateRefundStatus(orderDetailId: Long, refundStatusDto: RefundStatusDto)

}