package kea.dpang.order.feign

import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.feign.dto.UserDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "user-server")
fun interface UserServiceFeignClient {

    @GetMapping("/api/users/{userId}")
    fun getUserInfo(@PathVariable userId: Long): ResponseEntity<SuccessResponse<UserDto>>
}