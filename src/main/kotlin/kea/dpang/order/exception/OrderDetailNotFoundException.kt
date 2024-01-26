package kea.dpang.order.exception

class OrderDetailNotFoundException(id: Long) : RuntimeException("해당 ID ($id)에 대한 주문 상세 정보를 찾을 수 없습니다.")
