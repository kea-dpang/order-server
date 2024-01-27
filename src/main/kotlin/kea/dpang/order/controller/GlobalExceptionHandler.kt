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

    @ExceptionHandler(CancelNotFoundException::class)
    fun handleCancelNotFoundException(ex: CancelNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InsufficientMileageException::class)
    fun handleInsufficientMileageException(
        ex: InsufficientMileageException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InsufficientStockException::class)
    fun handleInsufficientStockException(
        ex: InsufficientStockException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidOrderStatusChangeException::class)
    fun handleInvalidOrderStatusChangeException(
        ex: InvalidOrderStatusChangeException?,
        request: WebRequest?
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidRefundStatusChangeException::class)
    fun handleInvalidRefundStatusChangeException(
        ex: InvalidRefundStatusChangeException?,
        request: WebRequest?
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ItemNotFoundException::class)
    fun handleItemNotFoundException(ex: ItemNotFoundException?, request: WebRequest?): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(OrderAlreadyInRequestedStatusException::class)
    fun handleOrderAlreadyInRequestedStatusException(
        ex: OrderAlreadyInRequestedStatusException?,
        request: WebRequest?
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(OrderDetailNotFoundException::class)
    fun handleOrderDetailNotFoundException(
        ex: OrderDetailNotFoundException?,
        request: WebRequest?
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(OrderNotFoundException::class)
    fun handleOrderNotFoundException(ex: OrderNotFoundException?, request: WebRequest?): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(RefundAlreadyInRequestedStatusException::class)
    fun handleRefundAlreadyInRequestedStatusException(
        ex: RefundAlreadyInRequestedStatusException?,
        request: WebRequest?
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(RefundNotFoundException::class)
    fun handleRefundNotFoundException(
        ex: RefundNotFoundException?,
        request: WebRequest?
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UnableToCancelException::class)
    fun handleUnableToCancelException(
        ex: UnableToCancelException?,
        request: WebRequest?
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UnableToRefundException::class)
    fun handleUnableToRefundException(
        ex: UnableToRefundException?,
        request: WebRequest?
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(ex!!, request!!, HttpStatus.BAD_REQUEST)
    }

    /**
     * ErrorResponse 객체를 생성하고 클라이언트에 반환하는 메서드
     *
     * @param ex 발생한 예외
     * @param request 클라이언트의 웹 요청 정보
     * @param status HTTP 상태 코드
     * @return 생성된 ErrorResponse 객체와 HTTP 상태 코드를 포함하는 ResponseEntity
     */
    private fun buildErrorResponse(
        ex: Exception,
        request: WebRequest,
        status: HttpStatus
    ): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = status.value(),
            error = status.name,
            message = (if (ex.message != null) ex.message else "세부 정보가 제공되지 않았습니다")!!,
            path = request.getDescription(false)
        )
        return ResponseEntity(errorMessage, status)
    }
}
