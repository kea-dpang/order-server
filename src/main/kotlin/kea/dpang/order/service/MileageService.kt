package kea.dpang.order.service

import kea.dpang.order.feign.dto.MileageDto

interface MileageService {

    /**
     * 사용자의 총 마일리지를 조회하는 메소드
     *
     * @param userId 마일리지 정보를 조회할 사용자의 ID.
     * @return 사용자의 총 마일리지
     */
    fun getUserMileage(userId: Long): Int

    /**
     * 사용자의 마일리지 정보를 조회하는 메소드
     *
     * @param userId 마일리지 정보를 조회할 사용자의 ID.
     * @return 조회된 마일리지 정보.
     */
    fun getUserMileageInfo(userId: Long): MileageDto

    /**
     * 사용자의 마일리지를 사용하는 메소드
     *
     * @param userId 마일리지를 사용할 사용자의 ID.
     * @param amount 사용할 마일리지의 양.
     * @param reason 마일리지 사용 사유.
     */
    fun consumeUserMileage(userId: Long, amount: Int, reason: String)

    /**
     * 사용자의 마일리지를 환불하는 메소드
     *
     * @param userId 마일리지를 환불할 사용자의 ID.
     * @param amount 환불할 마일리지의 양.
     * @param reason 마일리지 환불 사유.
     */
    fun refundMileage(userId: Long, amount: Int, reason: String)
}