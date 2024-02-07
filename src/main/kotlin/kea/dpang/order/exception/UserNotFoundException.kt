package kea.dpang.order.exception

class UserNotFoundException(userId: Long) : RuntimeException("아이디 ${userId}를 가진 사용자를 찾을 수 없습니다.")
