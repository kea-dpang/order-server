package kea.dpang.order.service

import kea.dpang.order.feign.UserServiceFeignClient
import kea.dpang.order.feign.dto.UserDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userServiceFeignClient: UserServiceFeignClient
) : UserService {

    private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun getUserInfo(userId: Long): UserDto {
        log.info("사용자 정보 조회 시작. 사용자 ID: {}", userId)
        val user = userServiceFeignClient.getUserInfo(userId).body!!.data
        log.info("사용자 정보 조회 완료.")

        return user
    }

    override fun getUserInfos(userIds: List<Long>): List<UserDto> {
        log.info("사용자 정보 조회 시작. 사용자 ID 목록: {}", userIds)
        val users = userServiceFeignClient.getUserInfos(userIds).body!!.data
        log.info("사용자 정보 조회 완료.")

        return users
    }
}