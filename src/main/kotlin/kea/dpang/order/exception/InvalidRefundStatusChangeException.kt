package kea.dpang.order.exception

class InvalidRefundStatusChangeException(currentStatus: String, targetStatus: String)
    : RuntimeException("환불 상태 변경이 유효하지 않습니다. 현재 상태: $currentStatus, 변경하려는 상태: $targetStatus")
