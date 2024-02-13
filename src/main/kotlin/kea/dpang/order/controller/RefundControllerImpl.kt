package kea.dpang.order.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.refund.RefundDetailDto
import kea.dpang.order.dto.refund.RefundDto
import kea.dpang.order.dto.refund.UpdateRefundStatusRequestDto
import kea.dpang.order.entity.RefundStatus
import kea.dpang.order.service.RefundService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "환불")
@RestController
@RequestMapping("/api/refunds")
class RefundControllerImpl(private val refundService: RefundService) : RefundController {

//    @PostAuthorize("(#role=='USER' and #returnObject.body.data.refundDto.userId==#clientId) or #role=='ADMIN' or #role=='SUPER_ADMIN'")
    @Operation(summary = "환불 정보 조회", description = "환불 정보를 조회합니다.")
    @GetMapping("/{refundId}")
    override fun getRefund(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ROLE") role: String,
        @Parameter(description = "환불 ID") @PathVariable refundId: Long
    ): ResponseEntity<SuccessResponse<RefundDetailDto>> {

        val refundDetail = refundService.getRefund(refundId)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "조회가 완료되었습니다.", refundDetail))
    }

//    @PreAuthorize("(#role=='USER' and #clientId==#userId) or #role=='ADMIN' or #role=='SUPER_ADMIN'")
    @Operation(summary = "환불 목록 조회", description = "조건에 맞는 환불 목록을 조회합니다.")
    @GetMapping
    override fun getRefundList(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ROLE") role: String,
        @Parameter(description = "환불 요청 시작 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate?,
        @Parameter(description = "환불 요청 종료 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate?,
        @Parameter(description = "환불 상태") @RequestParam(required = false) refundStatus: RefundStatus?,
        @Parameter(description = "사용자 ID") @RequestParam(required = false) userId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<RefundDto>>> {

        val refundList = refundService.getRefundList(startDate, endDate, refundStatus, userId, pageable)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "조회가 완료되었습니다.", refundList))
    }

//    @PreAuthorize("#role=='ADMIN' or #role=='SUPER_ADMIN'")
    @Operation(summary = "환불 상태 업데이트", description = "환불 상태를 업데이트합니다.")
    @PutMapping("/{refundId}")
    override fun updateRefundStatus(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ROLE") role: String,
        @Parameter(description = "환불 ID") @PathVariable refundId: Long,
        @Parameter(description = "환불 상태 정보") @RequestBody refundStatusDto: UpdateRefundStatusRequestDto
    ): ResponseEntity<BaseResponse> {

        refundService.updateRefundStatus(refundId, refundStatusDto)
        return ResponseEntity.ok(BaseResponse(HttpStatus.OK.value(), "환불 상태가 업데이트되었습니다."))
    }

}
