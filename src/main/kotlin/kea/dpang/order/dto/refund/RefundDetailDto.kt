package kea.dpang.order.dto.refund

import com.fasterxml.jackson.annotation.JsonUnwrapped

/**
 * 환불 조회 결과를 담는 DTO
 *
 * @property refundDto 환불 정보
 * @property retrievalInfo 회수처 정보
 * @property refundInfo 환불 정보
 * @property returnInfo 반품 정보
 */
data class RefundDetailDto(

    @JsonUnwrapped
    val refundDto: RefundDto, // 환불 정보
    val retrievalInfo: RetrievalInfo, // 회수처 정보
    val refundInfo: RefundInfo, // 환불 정보
    val returnInfo: ReturnInfo // 반품 정보
)
