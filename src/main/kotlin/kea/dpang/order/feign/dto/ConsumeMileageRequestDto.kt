package kea.dpang.order.feign.dto

/**
 * 마일리지 소비 요청에 대한 데이터를 담는 DTO.
 *
 * @property userId 마일리지 소비를 요청하는 사용자의 ID.
 * @property amount 소비할 마일리지의 양.
 * @property reason 마일리지 소비의 사유.
 */
data class ConsumeMileageRequestDto(val userId: Long, val amount: Int, val reason: String)
