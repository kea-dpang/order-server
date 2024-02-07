package kea.dpang.order.service

import kea.dpang.order.feign.dto.UserDto

interface UserService {

    fun getUserInfo(userId: Long): UserDto

    fun getUserInfos(userIds: List<Long>): List<UserDto>
}