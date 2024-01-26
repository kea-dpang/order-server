package kea.dpang.order.exception

class InsufficientMileageException(userId: Long) : RuntimeException("사용자(${userId})의 마일리지가 부족합니다.")
