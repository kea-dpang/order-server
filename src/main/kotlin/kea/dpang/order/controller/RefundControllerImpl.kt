package kea.dpang.order.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.refund.RefundDetailDto
import kea.dpang.order.dto.refund.RefundDto
import kea.dpang.order.dto.refund.RefundOrderRequestDto
import kea.dpang.order.dto.refund.RefundStatusDto
import kea.dpang.order.entity.RefundReason
import kea.dpang.order.service.RefundService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "환불")
@RestController
@RequestMapping("/api/refund")
class RefundControllerImpl(private val refundService: RefundService) : RefundController {

    @Operation(summary = "주문 환불 요청", description = "주문에 대한 환불을 요청합니다.")
    @PostMapping("/{orderDetailId}")
    override fun refundOrder(
        @Parameter(description = "주문 상세 ID") @PathVariable orderDetailId: Long,
        @Parameter(description = "환불 요청 정보") @RequestBody refundOrderRequest: RefundOrderRequestDto
    ): ResponseEntity<BaseResponse> {

        refundService.refundOrder(orderDetailId, refundOrderRequest)
        return ResponseEntity.ok(BaseResponse(HttpStatus.OK.value(), "환불 요청이 완료되었습니다."))
    }

    @Operation(summary = "환불 정보 조회", description = "환불 정보를 조회합니다.")
    @GetMapping("/{refundId}")
    override fun getRefund(
        @Parameter(description = "환불 ID") @PathVariable refundId: Long
    ): ResponseEntity<SuccessResponse<RefundDetailDto>> {

        val refundDetail = refundService.getRefund(refundId)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "조회가 완료되었습니다.", refundDetail))
    }

    @Operation(summary = "환불 목록 조회", description = "조건에 맞는 환불 목록을 조회합니다.")
    @GetMapping("/list")
    override fun getRefundList(
        @Parameter(description = "환불 요청 시작 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate?,
        @Parameter(description = "환불 요청 종료 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate?,
        @Parameter(description = "환불 사유") @RequestParam(required = false) refundReason: RefundReason?,
        @Parameter(description = "사용자 ID") @RequestParam(required = false) userId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<RefundDto>>> {

        val refundList = refundService.getRefundList(startDate, endDate, refundReason, userId, pageable)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "조회가 완료되었습니다.", refundList))
    }

    @Operation(summary = "환불 상태 업데이트", description = "환불 상태를 업데이트합니다.")
    @PutMapping("/{refundId}")
    override fun updateRefundStatus(
        @Parameter(description = "환불 ID") @PathVariable refundId: Long,
        @Parameter(description = "환불 상태 정보") @RequestBody refundStatusDto: RefundStatusDto
    ): ResponseEntity<BaseResponse> {

        refundService.updateRefundStatus(refundId, refundStatusDto)
        return ResponseEntity.ok(BaseResponse(HttpStatus.OK.value(), "환불 상태가 업데이트되었습니다."))
    }

}
