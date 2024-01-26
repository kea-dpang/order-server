package kea.dpang.order.exception

class UnableToCancelException : RuntimeException("결제 완료 상태의 주문만 취소할 수 있습니다.")