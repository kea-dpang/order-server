package kea.dpang.order.dto.order

import kea.dpang.order.feign.dto.MileageDto

/**
 * 마일리지 정보를 담는 DTO
 *
 * @property mileage 사원 마일리지
 * @property personalChargedMileage 개인 충전 마일리지
 */
data class MileageInfo(
    var mileage: Int,
    var personalChargedMileage: Int
) {
    companion object {
        fun from(mileage: MileageDto): MileageInfo {
            return MileageInfo(mileage.mileage, mileage.personalChargedMileage)
        }
    }
}