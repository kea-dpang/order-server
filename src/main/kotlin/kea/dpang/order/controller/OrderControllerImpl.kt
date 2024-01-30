package kea.dpang.order.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.order.OrderDetailDto
import kea.dpang.order.dto.order.OrderDto
import kea.dpang.order.dto.order.OrderRequestDto
import kea.dpang.order.dto.order.UpdateOrderStatusRequestDto
import kea.dpang.order.entity.OrderStatus
import kea.dpang.order.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "주문")
@RestController
@RequestMapping("/api/order")
class OrderControllerImpl(private val orderService: OrderService) : OrderController {

    @Operation(summary = "주문하기", description = "새로운 주문을 등록합니다.")
    @PostMapping
    override fun placeOrder(
        @Parameter(description = "사용자 ID") @RequestHeader("X-DPANG-CLIENT-ID") userId: Long,
        @Parameter(description = "주문 요청 정보") @RequestBody orderRequest: OrderRequestDto
    ): ResponseEntity<SuccessResponse<OrderDto>> {

        val orderResult = orderService.placeOrder(userId, orderRequest)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "주문이 완료되었습니다.", orderResult))
    }

    @Operation(summary = "주문 상태 수정", description = "주문의 상태를 수정합니다.")
    @PutMapping("/{orderId}")
    override fun updateOrderStatus(
        @Parameter(description = "주문 ID") @PathVariable orderId: Long,
        @Parameter(description = "주문 상태 정보") @RequestBody updateOrderStatusRequest: UpdateOrderStatusRequestDto
    ): ResponseEntity<BaseResponse> {

        orderService.updateOrderStatus(orderId, updateOrderStatusRequest)
        return ResponseEntity.ok(BaseResponse(HttpStatus.OK.value(), "주문 상태가 수정되었습니다."))
    }

    @Operation(summary = "주문 목록 조회", description = "조건에 맞는 주문 목록을 조회합니다.")
    @GetMapping("/list")
    override fun getOrderList(
        @Parameter(description = "주문 시작 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate?,
        @Parameter(description = "주문 종료 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate?,
        @Parameter(description = "주문 상태") @RequestParam(required = false) orderStatus: OrderStatus?,
        @Parameter(description = "주문 ID") @RequestParam(required = false) orderId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<OrderDto>>> {

        val orderList = orderService.getOrderList(startDate, endDate, orderId, pageable)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "조회가 완료되었습니다.", orderList))
    }

    @Operation(summary = "주문 상세 조회", description = "주문의 상세 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    override fun getOrder(
        @Parameter(description = "주문 ID") @PathVariable orderId: Long
    ): ResponseEntity<SuccessResponse<OrderDetailDto>> {

        val orderDetail = orderService.getOrder(orderId)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "조회가 완료되었습니다.", orderDetail))
    }

}
