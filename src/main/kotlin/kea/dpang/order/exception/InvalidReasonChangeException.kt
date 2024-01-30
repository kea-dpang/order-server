package kea.dpang.order.exception

class InvalidReasonChangeException(reason: String) : IllegalArgumentException("유효하지 않은 Reason 값: $reason")
