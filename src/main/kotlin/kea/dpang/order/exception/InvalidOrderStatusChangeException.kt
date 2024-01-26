package kea.dpang.order.exception

class InvalidOrderStatusChangeException(currentStatus: String, targetStatus: String)
    : RuntimeException("$currentStatus -> $targetStatus 는 유효하지 않은 주문 상태 변경입니다.")
