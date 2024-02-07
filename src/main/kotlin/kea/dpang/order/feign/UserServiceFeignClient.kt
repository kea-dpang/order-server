package kea.dpang.order.feign

import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.feign.dto.UserDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "user-server")
interface UserServiceFeignClient {

    @GetMapping("/api/users/{userId}")
    fun getUserInfo(@PathVariable userId: Long): ResponseEntity<SuccessResponse<UserDto>>

    @GetMapping("/api/users/list")
    fun getUserInfos(
        @RequestParam userIds: List<Long>
    ): ResponseEntity<SuccessResponse<List<UserDto>>>
}