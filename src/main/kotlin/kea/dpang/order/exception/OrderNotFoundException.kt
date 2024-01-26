package kea.dpang.order.exception

class OrderNotFoundException(orderId: Long) : RuntimeException("주문 번호 $orderId 의 주문이 존재하지 않습니다.")
