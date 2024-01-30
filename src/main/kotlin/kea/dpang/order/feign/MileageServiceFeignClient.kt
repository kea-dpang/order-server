package kea.dpang.order.feign

import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.feign.dto.ConsumeMileageRequestDto
import kea.dpang.order.feign.dto.MileageDto
import kea.dpang.order.feign.dto.RefundMileageRequestDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@FeignClient(name = "mileage-server")
interface MileageServiceFeignClient {

    /**
     * 사용자의 마일리지를 조회하는 메서드
     *
     * @param clientId 클라이언트 ID
     * @param userId 마일리지를 조회할 사용자의 ID
     * @return 마일리지 정보가 담긴 응답 객체
     */
    @GetMapping("/{userId}")
    fun getUserMileage(
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @PathVariable userId: Long
    ): ResponseEntity<SuccessResponse<MileageDto>>

    /**
     * 사용자의 마일리지를 소비하는 메서드
     *
     * @param clientId 클라이언트 ID
     * @param request 마일리지 소비 요청 정보가 담긴 DTO
     * @return 요청의 처리 결과를 나타내는 응답 객체
     */
    @PostMapping("/consume")
    fun consumeMileage(
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @RequestBody request: ConsumeMileageRequestDto
    ): ResponseEntity<BaseResponse>

    /**
     * 사용자의 마일리지를 환불하는 메서드
     *
     * @param clientId 클라이언트 ID
     * @param request 마일리지 환불 요청 정보를 담은 DTO.
     * @return 요청의 처리 결과를 나타내는 응답 객체
     */
    @PostMapping("/refund")
    fun refundMileage(
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @RequestBody request: RefundMileageRequestDTO
    ): ResponseEntity<BaseResponse>
}
