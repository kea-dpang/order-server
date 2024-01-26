package kea.dpang.order.exception

class RefundNotFoundException(refundId: Long) : RuntimeException("해당 ID ($refundId)에 대한 환불 정보를 찾을 수 없습니다.")
