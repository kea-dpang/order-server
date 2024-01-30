package kea.dpang.order.exception

class InvalidOrderStatusException(status: String) : IllegalArgumentException("유효하지 않은 OrderStatus 값: $status")
