package kea.dpang.order.service

import kea.dpang.order.feign.MileageServiceFeignClient
import kea.dpang.order.feign.dto.ConsumeMileageRequestDto
import kea.dpang.order.feign.dto.RefundMileageRequestDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MileageServiceImpl(
    private val mileageServiceFeignClient: MileageServiceFeignClient
) : MileageService {

    private val log = LoggerFactory.getLogger(MileageServiceImpl::class.java)

    override fun getUserMileage(userId: Long): Int {
        log.info("마일리지 조회 시작. 사용자 ID: {}", userId)
        val mileage = mileageServiceFeignClient.getUserMileage(userId, userId).body!!.data
        log.info("마일리지 조회 완료.")

        return mileage.mileage + mileage.personalChargedMileage
    }

    override fun consumeUserMileage(userId: Long, amount: Int, reason: String) {
        val consumeMileageRequest = ConsumeMileageRequestDto(
            userId = userId,
            amount = amount,
            reason = reason
        )

        log.info("마일리지 사용 요청 시작. 사용자 ID: {}, 사용량: {}, 사유: {}", userId, amount, reason)
        mileageServiceFeignClient.consumeMileage(userId, consumeMileageRequest)
        log.info("마일리지 사용 요청 완료.")
    }

    override fun refundMileage(userId: Long, amount: Int, reason: String) {
        val refundMileageRequestDTO = RefundMileageRequestDTO(
            userId = userId,
            amount = amount,
            reason = reason
        )

        log.info("마일리지 환불 요청 시작. 사용자 ID: {}, 환불량: {}, 사유: {}", userId, amount, reason)
        mileageServiceFeignClient.refundMileage(userId, refundMileageRequestDTO)
        log.info("마일리지 환불 요청 완료.")
    }
}