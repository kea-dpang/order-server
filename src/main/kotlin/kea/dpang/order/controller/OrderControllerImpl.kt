package kea.dpang.order.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.dto.OrderedProductInfo
import kea.dpang.order.dto.order.*
import kea.dpang.order.dto.refund.RefundOrderRequestDto
import kea.dpang.order.entity.OrderStatus
import kea.dpang.order.service.CancelService
import kea.dpang.order.service.OrderService
import kea.dpang.order.service.RefundService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "주문")
@RestController
@RequestMapping("/api/orders")
class OrderControllerImpl(
    private val orderService: OrderService,
    private val cancelService: CancelService,
    private val refundService: RefundService
) : OrderController {

    @Operation(summary = "주문하기", description = "새로운 주문을 등록합니다.")
    @PostMapping
    override fun placeOrder(
        @Parameter(description = "사용자 ID") @RequestHeader("X-DPANG-CLIENT-ID") userId: Long,
        @Parameter(description = "주문 요청 정보") @RequestBody orderRequest: OrderRequestDto
    ): ResponseEntity<SuccessResponse<OrderResponseDto>> {

        val orderResult = orderService.placeOrder(userId, orderRequest)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "주문이 완료되었습니다.", orderResult))
    }

    @Operation(summary = "주문 상태 수정", description = "주문 전체의 상태를 수정합니다.")
    @PutMapping("/{orderId}")
    override fun updateOrderStatus(
        @Parameter(description = "주문 ID") @PathVariable orderId: Long,
        @Parameter(description = "변경할 주문 상태 정보") @RequestBody updateOrderStatusRequest: UpdateOrderStatusRequestDto
    ): ResponseEntity<BaseResponse> {

        orderService.updateOrderStatus(orderId, updateOrderStatusRequest)
        return ResponseEntity.ok(BaseResponse(HttpStatus.OK.value(), "주문 상태가 수정되었습니다."))
    }

    @Operation(summary = "주문 상품 상태 수정", description = "주문 내의 특정 상품의 상태를 수정합니다.")
    @PutMapping("/{orderId}/details/{orderDetailId}")
    override fun updateOrderItemStatus(
        @Parameter(description = "주문 ID") @PathVariable orderId: Long,
        @Parameter(description = "주문 상세 ID") @PathVariable orderDetailId: Long,
        @Parameter(description = "변경할 주문 상태 정보") @RequestBody updateOrderStatusRequest: UpdateOrderStatusRequestDto
    ): ResponseEntity<BaseResponse> {

        orderService.updateOrderDetailStatus(orderId, orderDetailId, updateOrderStatusRequest)
        return ResponseEntity.ok(BaseResponse(HttpStatus.OK.value(), "주문 항목의 상태가 수정되었습니다."))
    }

    @Operation(summary = "주문 목록 조회", description = "조건에 맞는 주문 목록을 조회합니다.")
    @GetMapping
    override fun getOrderList(
        @Parameter(description = "주문 시작 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate?,
        @Parameter(description = "주문 종료 날짜") @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate?,
        @Parameter(description = "주문 상태") @RequestParam(required = false) orderStatus: OrderStatus?,
        @Parameter(description = "사용자 ID") @RequestParam(required = false) userId: Long?,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<OrderDto>>> {

        val orderList = orderService.getOrderList(startDate, endDate, userId, pageable)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "주문 정보가 조회 완료되었습니다.", orderList))
    }

    @Operation(summary = "주문 조회", description = "주문 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    override fun getOrder(
        @Parameter(description = "주문 ID") @PathVariable orderId: Long
    ): ResponseEntity<SuccessResponse<OrderDetailDto>> {

        val orderDetail = orderService.getOrderInfo(orderId)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "주문 정보가 조회 완료되었습니다.", orderDetail))
    }

    @Operation(summary = "주문 상세 조회", description = "주문 상세 정보를 조회합니다.")
    @GetMapping("/{orderId}/details/{orderDetailId}")
    override fun getOrderDetail(
        @Parameter(description = "주문 ID") @PathVariable orderId: Long,
        @Parameter(description = "주문 상세 ID") @PathVariable orderDetailId: Long
    ): ResponseEntity<SuccessResponse<OrderedProductInfo>> {

        val orderDetail = orderService.getOrderDetailInfo(orderId, orderDetailId)
        return ResponseEntity.ok(SuccessResponse(HttpStatus.OK.value(), "주문 상세 정보가 조회 완료되었습니다.", orderDetail))
    }

    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @PostMapping("/{orderId}/details/{orderDetailId}/cancel")
    override fun cancelOrder(
        @Parameter(description = "주문 ID") @PathVariable orderId: Long,
        @Parameter(description = "주문 상세 ID") @PathVariable orderDetailId: Long
    ): ResponseEntity<BaseResponse> {

        cancelService.cancelOrder(orderId, orderDetailId)
        return ResponseEntity.ok(BaseResponse(HttpStatus.OK.value(), "주문이 취소되었습니다."))
    }

    @Operation(summary = "주문 환불 요청", description = "주문에 대한 환불을 요청합니다.")
    @PostMapping("/{orderId}/details/{orderDetailId}/refund")
    override fun refundOrder(
        @Parameter(description = "주문 ID") @PathVariable orderId: Long,
        @Parameter(description = "주문 상세 ID") @PathVariable orderDetailId: Long,
        @Parameter(description = "환불 요청 정보") @RequestBody refundOrderRequest: RefundOrderRequestDto
    ): ResponseEntity<BaseResponse> {

        refundService.refundOrder(orderId, orderDetailId, refundOrderRequest)
        return ResponseEntity.ok(BaseResponse(HttpStatus.OK.value(), "환불 요청이 완료되었습니다."))
    }

}
