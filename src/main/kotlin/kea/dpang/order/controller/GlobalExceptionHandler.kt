package kea.dpang.order.controller

import kea.dpang.order.base.ErrorResponse
import kea.dpang.order.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(
        CancelNotFoundException::class,
        ItemNotFoundException::class,
        OrderDetailNotFoundException::class,
        OrderNotFoundException::class,
        RefundNotFoundException::class,
        ProductNotFoundException::class
    )
    fun handleNotFoundException(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> {
        return generateErrorResponse(ex, request, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(
        InsufficientMileageException::class,
        InsufficientStockException::class,
        InvalidOrderStatusChangeException::class,
        InvalidRefundStatusChangeException::class,
        OrderAlreadyInRequestedStatusException::class,
        RefundAlreadyInRequestedStatusException::class,
        UnableToCancelException::class,
        UnableToRefundException::class,
        InvalidOrderStatusException::class,
        InvalidReasonException::class
    )
    fun handleBadRequestException(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> {
        return generateErrorResponse(ex, request, HttpStatus.BAD_REQUEST)
    }

    private fun generateErrorResponse(
        ex: RuntimeException,
        request: WebRequest,
        status: HttpStatus
    ): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = status.value(),
            error = status.name,
            message = ex.message ?: "세부 정보가 제공되지 않았습니다",
            path = request.getDescription(false)
        )

        return ResponseEntity(errorMessage, status)
    }
}
