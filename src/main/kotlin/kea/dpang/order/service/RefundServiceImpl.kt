package kea.dpang.order.service.impl

import kea.dpang.order.dto.refund.RefundDetailDto
import kea.dpang.order.dto.refund.RefundDto
import kea.dpang.order.dto.refund.RefundOrderRequestDto
import kea.dpang.order.dto.refund.RefundStatusDto
import kea.dpang.order.entity.Reason
import kea.dpang.order.repository.RefundRepository
import kea.dpang.order.service.RefundService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RefundServiceImpl(private val refundRepository: RefundRepository) : RefundService {

    override fun refundOrder(orderDetailId: String, refundOrderRequest: RefundOrderRequestDto) {
        TODO("Not yet implemented")
        /*
            1. orderDetailId를 사용하여 데이터베이스에서 주문 상세 정보를 조회한다. 이때, 주문 상세 정보가 없으면 예외를 발생시킨다.
            2. 조회된 주문 상태를 확인하여, 환불이 가능한 상태인지 확인한다 (주문이 이미 환불 요청이나 취소된 상태인지). 환불이 불가능한 상태라면 예외를 발생시킨다.
            3. RefundOrderRequestDto에서 환불 요청 정보를 추출하고 환불 객체를 생성한다 (refundStatus - REFUND_REQUEST)
            4. 환불 요청 정보를 데이터베이스에 저장한다.
         */
    }

    override fun getRefund(orderId: String): RefundDetailDto {
        TODO("Not yet implemented")
        /*
            1. orderId를 사용하여 데이터베이스에서 환불 상세 정보를 조회한다. 이때, 환불 상세 정보가 없으면 예외를 발생시킨다.
            2. 조회된 환불 상세 정보를 RefundDetailDto로 변환 및 반환한다.
         */
    }

    override fun getRefundList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        reason: Reason?,
        refundId: String?,
        pageable: Pageable
    ): Page<RefundDto> {
        TODO("Not yet implemented")
        /*
            1. startDate, endDate, reason, refundId와 같은 필터링 조건을 사용하여 데이터베이스에서 환불 정보 목록을 조회한다. 이때, pageable 객체를 사용하여 페이징 처리를 적용한다.
            2. 조회된 환불 정보 목록을 RefundDto로 변환한다. 각 환불 정보를 RefundDto로 변환하는 로직을 목록의 각 요소에 적용한다.
            3. 변환된 RefundDto 목록을 Page 객체로 래핑하여 반환한다. 이때, 원본 Page 객체의 메타데이터(예: 총 페이지 수, 현재 페이지 번호 등)를 유지해야 한다.
         */
    }

    override fun updateRefundStatus(orderDetailId: String, refundStatusDto: RefundStatusDto) {
        TODO("Not yet implemented")
        /*
            1. orderDetailId를 사용하여 데이터베이스에서 환불 상세 정보와 주문 상세 정보를 조회한다. 이때, 환불 정보나 주문 상세 정보가 없으면 예외를 발생시킨다.
            2. RefundStatusDto에서 변경할 환불 상태를 추출한다.
            3. 변경할 환불 상태가 현재 환불 상태와 동일한지 확인한다. 만약 동일하다면 상태 변경을 진행하지 않고 메소드를 종료한다.
            4. 환불 상태 변경이 유효한지 검증한다. 환불 상태를 '반품 완료'로 변경하려면 현재 환불 상태가 '회수 중'이어야 한다.
            5. 환불 상태 변경이 유효하다면 환불 상세 정보의 상태를 변경한다.
            5-1. 변경할 환불 상태가 '반품 완료'인 경우, 주문에 사용된 마일리지를 사용자에게 환불하고, 환불된 상품의 재고를 증가시킨다.
            5-2. 변경된 환불 상세 정보를 데이터베이스에 업데이트한다.
         */
    }
}
