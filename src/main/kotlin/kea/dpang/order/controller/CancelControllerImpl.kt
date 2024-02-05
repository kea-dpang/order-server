package kea.dpang.order.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.cancel.CancelDto
import kea.dpang.order.service.CancelService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "취소")
@RestController
@RequestMapping("/api/cancel")
class CancelControllerImpl(private val cancelService: CancelService) : CancelController {

    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @PostMapping("/{orderDetailId}")
    override fun cancelOrder(
        @Parameter(description = "주문 상세 ID") @PathVariable orderDetailId: Long
    ): ResponseEntity<BaseResponse> {

        cancelService.cancelOrder(orderDetailId)
        return ResponseEntity.ok(BaseResponse(HttpStatus.OK.value(), "주문이 취소되었습니다."))
    }

    @Operation(summary = "취소 정보 조회", description = "취소 정보를 조회합니다.")
    @GetMapping("/{cancelId}")
    override fun getCancel(
        @Parameter(description = "취소 ID") @PathVariable cancelId: Long
    ): ResponseEntity<SuccessResponse<CancelDto>> {

        val cancelInfo = cancelService.getCancel(cancelId)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "조회가 완료되었습니다.", cancelInfo))
    }

    @Operation(summary = "취소 목록 조회", description = "조건에 맞는 취소 목록을 조회합니다.")
    @GetMapping("/list")
    override fun getCancelList(
        @Parameter(description = "취소 요청 시작 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate?,
        @Parameter(description = "취소 요청 종료 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate?,
        @Parameter(description = "사용자 ID") @RequestParam(required = false) userId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<CancelDto>>> {

        val cancelList = cancelService.getCancelList(startDate, endDate, userId, pageable)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "조회가 완료되었습니다.", cancelList))
    }

}
