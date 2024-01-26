package kea.dpang.order.exception

class CancelNotFoundException(id: Long) : RuntimeException("해당 ID ($id)에 대한 취소 정보를 찾을 수 없습니다.")
