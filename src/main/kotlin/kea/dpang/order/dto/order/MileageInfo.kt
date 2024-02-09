package kea.dpang.order.dto.order

import kea.dpang.order.feign.dto.MileageDto

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