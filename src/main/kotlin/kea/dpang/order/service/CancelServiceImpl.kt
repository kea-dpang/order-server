package kea.dpang.order.service

import kea.dpang.order.dto.cancel.CancelDto
import kea.dpang.order.entity.Reason
import kea.dpang.order.repository.CancelRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CancelServiceImpl(private val cancelRepository: CancelRepository) : CancelService {

    override fun cancelOrder(orderDetailId: String) {
        TODO("Not yet implemented")
        /*
            1. 주문 상세 ID를 사용하여 주문 상세 정보를 조회한다.
            2. 조회된 주문 상태를 확인하여, 주문이 배송 중인지 확인한다. 배송 중이 진행된 경우 취소 요청은 처리할 수 없다.
            3. 주문 상태를 '취소'로 변경한다.
            4. 취소된 주문에 포함된 상품의 개수를 상품 서비스에 요청하여 재고를 증가시킨다.
            5. 주문에 사용된 마일리지를 계산하고, 마일리지 서비스에 요청하여 사용자에게 환불한다.
         */
    }

    override fun getCancel(cancelId: String): CancelDto {
        TODO("Not yet implemented")
        /*
            1. 주어진 cancelId를 사용하여 데이터베이스에서 취소 정보를 조회한다. 이때, 취소 정보가 없으면 예외를 발생시킵니다.
            2. 조회된 취소 정보를 CancelDto로 변환 및 반환한다.
         */
    }

    override fun getCancelList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        reason: Reason?,
        cancelId: String?,
        pageable: Pageable
    ): Page<CancelDto> {
        TODO("Not yet implemented")
        /*
            1. startDate, endDate, reason, cancelId와 같은 필터링 조건을 사용하여 데이터베이스에서 취소 정보 목록을 조회한다. 이때, pageable 객체를 사용하여 페이징 처리를 적용한다.
            2. 조회된 취소 정보 목록을 CancelDto로 변환한다. 각 취소 정보를 CancelDto로 변환하는 로직을 목록의 각 요소에 적용한다.
            3. 변환된 CancelDto 목록을 Page 객체로 래핑하여 반환한다. 이때, 원본 Page 객체의 메타데이터(예: 총 페이지 수, 현재 페이지 번호 등)를 유지해야 한다.
         */
    }
}
