package kea.dpang.order.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class InsufficientStockException(
    productId: Long
) : RuntimeException("상품 ID ${productId}의 재고가 충분하지 않습니다.")
