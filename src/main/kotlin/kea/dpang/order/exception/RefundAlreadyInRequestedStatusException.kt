package kea.dpang.order.exception

class RefundAlreadyInRequestedStatusException : RuntimeException("변경하려는 환불 상태가 현재 환불 상태와 동일합니다.")
