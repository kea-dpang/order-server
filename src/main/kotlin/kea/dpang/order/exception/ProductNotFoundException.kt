package kea.dpang.order.exception

class ProductNotFoundException(
    productId: Long
) : RuntimeException("상품 정보를 찾을 수 없습니다. 상품 ID: $productId")
