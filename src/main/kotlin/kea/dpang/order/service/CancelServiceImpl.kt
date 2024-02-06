package kea.dpang.order.service

import kea.dpang.order.dto.OrderedProductInfo
import kea.dpang.order.dto.ProductInfoDto
import kea.dpang.order.dto.cancel.CancelDto
import kea.dpang.order.entity.Cancel
import kea.dpang.order.entity.OrderStatus.CANCELLED
import kea.dpang.order.entity.OrderStatus.PAYMENT_COMPLETED
import kea.dpang.order.exception.CancelNotFoundException
import kea.dpang.order.exception.OrderDetailNotFoundException
import kea.dpang.order.exception.UnableToCancelException
import kea.dpang.order.feign.ItemServiceFeignClient
import kea.dpang.order.feign.MileageServiceFeignClient
import kea.dpang.order.feign.dto.RefundMileageRequestDTO
import kea.dpang.order.feign.dto.UpdateStockListRequestDto
import kea.dpang.order.feign.dto.UpdateStockRequestDto
import kea.dpang.order.repository.CancelRepository
import kea.dpang.order.repository.OrderDetailRepository
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(CancelServiceImpl::class.java)

    override fun cancelOrder(orderDetailId: Long) {
        log.info("주문 취소 시작. 주문 상세 ID: {}", orderDetailId)

        // 주문 상세 ID를 사용하여 주문 상세 정보를 조회한다.
        val orderDetail = orderDetailRepository.findById(orderDetailId)
            .orElseThrow {
                log.error("주문 상세 정보를 찾을 수 없음. 주문 상세 ID: {}", orderDetailId)
                OrderDetailNotFoundException(orderDetailId)
            }

        // 조회된 주문 상태를 확인하여, 주문이 취소가 가능한지 확인한다. 주문 상태가 '결제 완료'인 경우에만 취소가 가능하다.
        if (orderDetail.status != PAYMENT_COMPLETED) {
            log.error("주문 취소 불가능 상태. 주문 상세 ID: {}", orderDetailId)
            throw UnableToCancelException()
        }

        // 주문 상태를 '취소'로 변경한다.
        orderDetail.status = CANCELLED

        // 주문 취소 정보
        val cancel = Cancel(
            orderDetail = orderDetail
        )

        // 취소 정보를 데이터베이스에 저장한다.
        cancelRepository.save(cancel)
        log.info("주문 취소 완료. 주문 상세 ID: {}", orderDetailId)

        // 주문 상세 정보에 취소 정보를 연관 관계 편의 메서드를 사용하여 추가한다.
        orderDetail.assignCancel(cancel)

        // 취소된 주문에 포함된 상품의 개수를 상품 서비스에 요청하여 재고를 증가시킨다.
        itemServiceFeignClient.updateStock(
            UpdateStockListRequestDto(
                listOf(
                    UpdateStockRequestDto(
                        itemId = orderDetail.itemId,
                        quantity = orderDetail.quantity
                    )
                )
            )
        )
        log.info("재고 증가 요청 완료. 상품 ID: {}, 증가량: {}", orderDetail.itemId, orderDetail.quantity)

        // 주문에 사용된 마일리지를 마일리지 서비스에 요청하여 사용자에게 환불한다.
        val refundMileageInfo = RefundMileageRequestDTO(
            userId = orderDetail.order.userId,
            amount = orderDetail.order.productPaymentAmount + orderDetail.order.deliveryFee,
            reason = "주문 취소"
        )
        mileageServiceFeignClient.refundMileage(orderDetail.order.userId, refundMileageInfo)
        log.info("마일리지 환불 요청 완료. 사용자 ID: {}, 환불 금액: {}", orderDetail.order.userId, orderDetail.order.productPaymentAmount)

        log.info("주문 취소 완료. 주문 상세 ID: {}", orderDetailId)
    }

    @Transactional(readOnly = true)
    override fun getCancel(cancelId: Long): CancelDto {
        log.info("취소 정보 조회 시작. 취소 ID: {}", cancelId)

        // 주어진 cancelId를 사용하여 데이터베이스에서 취소 정보를 조회한다.
        val cancel = cancelRepository.findById(cancelId)
            .orElseThrow {
                log.error("취소 정보를 찾을 수 없음. 취소 ID: {}", cancelId)
                CancelNotFoundException(cancelId)
            }

        log.info("취소 정보 조회 완료. 취소 ID: {}", cancelId)

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
        log.info("취소 정보 변환 시작. 취소 ID: {}", cancel.id)

        // 상품 정보를 조회한다.
        val orderDetail = cancel.orderDetail
        val product = itemServiceFeignClient.getItemInfo(orderDetail.itemId).body!!.data

        // 상품 정보, 주문 상세 정보, 취소 정보를 바탕으로 OrderedProductInfo를 생성한다.
        val orderedProductInfo = OrderedProductInfo(
            orderDetailId = orderDetail.id!!,
            orderStatus = orderDetail.status,
            productInfoDto = ProductInfoDto.from(product),
            productQuantity = orderDetail.quantity
        )

        // Cancel 엔티티와 OrderedProductInfo를 사용하여 CancelDto를 생성한다.
        val cancelDto = CancelDto(
            cancelId = cancel.id!!,
            cancelRequestDate = cancel.requestDate!!.toLocalDate(),
            orderId = orderDetail.order.id!!,
            orderDate = orderDetail.order.date!!.toLocalDate(),
            product = orderedProductInfo,
            expectedRefundAmount = product.price * orderDetail.quantity
        )

        log.info("취소 정보 변환 완료. 취소 ID: {}", cancel.id)

        return cancelDto
    }

    @Transactional(readOnly = true)
    override fun getCancelList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        userId: Long?,
        pageable: Pageable
    ): Page<CancelDto> {
        log.info("취소 목록 조회 시작. 시작 날짜: {}, 종료 날짜: {}, 사용자 ID: {}, 페이지 정보: {}", startDate, endDate, userId, pageable)

        val cancelList = cancelRepository
            .findCancels(startDate, endDate, userId, pageable)
            .map { convertCancelEntityToDto(it) }

        log.info("취소 목록 조회 완료. 조회된 취소 건수: {}", cancelList.totalElements)

        return cancelList
    }
}
