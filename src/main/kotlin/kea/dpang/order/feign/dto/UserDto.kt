package kea.dpang.order.feign.dto

import java.time.LocalDate

data class UserDto(
    val userId: Long,
    val employeeNumber: Long,
    val name: String,
    val email: String,
    val joinDate: LocalDate
)