package kea.dpang.order.feign.dto

/**
 * 마일리지 환불 요청에 대한 데이터를 담는 DTO.
 *
 * @property userId 마일리지 환불을 요청하는 사용자의 ID.
 * @property amount 환불할 마일리지의 양.
 * @property reason 마일리지 환불의 사유.
 */
data class RefundMileageRequestDTO(val userId: Long, val amount: Int, val reason: String)