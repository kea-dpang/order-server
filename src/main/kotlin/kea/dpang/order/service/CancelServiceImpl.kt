package kea.dpang.order.service

import kea.dpang.order.dto.OrderedProductInfo
import kea.dpang.order.dto.ProductInfoDto
import kea.dpang.order.dto.cancel.CancelDto
import kea.dpang.order.entity.Cancel
import kea.dpang.order.entity.OrderStatus.CANCELLED
import kea.dpang.order.entity.OrderStatus.PAYMENT_COMPLETED
import kea.dpang.order.entity.Reason
import kea.dpang.order.exception.CancelNotFoundException
import kea.dpang.order.exception.OrderDetailNotFoundException
import kea.dpang.order.exception.UnableToCancelException
import kea.dpang.order.feign.ItemServiceFeignClient
import kea.dpang.order.feign.MileageServiceFeignClient
import kea.dpang.order.feign.dto.RefundMileageRequestDTO
import kea.dpang.order.repository.CancelRepository
import kea.dpang.order.repository.OrderDetailRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class CancelServiceImpl(
    private val orderDetailRepository: OrderDetailRepository,
    private val cancelRepository: CancelRepository,
    private val itemServiceFeignClient: ItemServiceFeignClient,
    private val mileageServiceFeignClient: MileageServiceFeignClient
) : CancelService {

    override fun cancelOrder(orderDetailId: Long, reason: Reason) {
        // 주문 상세 ID를 사용하여 주문 상세 정보를 조회한다.
        val orderDetail = orderDetailRepository.findById(orderDetailId)
            .orElseThrow { OrderDetailNotFoundException(orderDetailId) }

        // 조회된 주문 상태를 확인하여, 주문이 취소가 가능한지 확인한다. 주문 상태가 '결제 완료'인 경우에만 취소가 가능하다.
        if (orderDetail.status != PAYMENT_COMPLETED) {
            throw UnableToCancelException()
        }

        // 주문 상태를 '취소'로 변경한다.
        orderDetail.status = CANCELLED

        // 주문 취소 정보
        val cancel = Cancel(
            orderDetail = orderDetail,
            reason = reason
        )

        // 취소 정보를 데이터베이스에 저장한다.
        cancelRepository.save(cancel)

        // 주문 상세 정보에 취소 정보를 연관 관계 편의 메서드를 사용하여 추가한다.
        orderDetail.assignCancel(cancel)

        // 취소된 주문에 포함된 상품의 개수를 상품 서비스에 요청하여 재고를 증가시킨다.
        itemServiceFeignClient.increaseItemStock(orderDetail.itemId, orderDetail.quantity)

        // 주문에 사용된 마일리지를 마일리지 서비스에 요청하여 사용자에게 환불한다.
        val refundMileageInfo = RefundMileageRequestDTO(
            userId = orderDetail.order.userId,
            amount = orderDetail.order.productPaymentAmount,
            reason = "주문 취소"
        )
        mileageServiceFeignClient.refundMileage(orderDetail.order.userId, refundMileageInfo)
    }

    @Transactional(readOnly = true)
    override fun getCancel(cancelId: Long): CancelDto {
        // 주어진 cancelId를 사용하여 데이터베이스에서 취소 정보를 조회한다.
        val cancel = cancelRepository.findById(cancelId)
            .orElseThrow { CancelNotFoundException(cancelId) }

        // Cancel 엔티티를 CancelDto로 변환하고 반환한다.
        return convertCancelEntityToDto(cancel)
    }

    /**
     * Cancel 엔티티를 CancelDto로 변환하는 메서드
     *
     * @param cancel 변환할 Cancel 엔티티 객체
     * @return 변환된 CancelDto 객체
     */
    private fun convertCancelEntityToDto(cancel: Cancel): CancelDto {
        // 상품 정보를 조회한다.
        val orderDetail = cancel.orderDetail
        val product = itemServiceFeignClient.getItemInfo(orderDetail.itemId).data

        // 상품 정보, 주문 상세 정보, 취소 정보를 바탕으로 OrderedProductInfo를 생성한다.
        val orderedProductInfo = OrderedProductInfo(
            orderDetailId = orderDetail.id!!,
            orderStatus = orderDetail.status,
            productInfoDto = ProductInfoDto.from(product),
            productQuantity = orderDetail.quantity
        )

        // Cancel 엔티티와 OrderedProductInfo를 사용하여 CancelDto를 생성한다.
        return CancelDto(
            cancelId = cancel.id!!,
            cancelRequestDate = cancel.requestDate!!.toLocalDate(),
            orderId = orderDetail.order.id!!,
            orderDate = orderDetail.order.date!!.toLocalDate(),
            product = orderedProductInfo,
            expectedRefundAmount = product.price * orderDetail.quantity
        )
    }

    @Transactional(readOnly = true)
    override fun getCancelList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        reason: Reason?,
        cancelId: Long?,
        pageable: Pageable
    ): Page<CancelDto> {

        return cancelRepository
            .findCancels(startDate, endDate, reason, cancelId, pageable)
            .map { convertCancelEntityToDto(it) }
    }
}
