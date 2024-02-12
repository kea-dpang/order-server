package kea.dpang.order.service

import kea.dpang.order.dto.ProductInfoDto
import kea.dpang.order.dto.cancel.CancelDto
import kea.dpang.order.entity.Cancel
import kea.dpang.order.entity.OrderDetail
import kea.dpang.order.entity.OrderStatus.CANCELLED
import kea.dpang.order.entity.OrderStatus.PAYMENT_COMPLETED
import kea.dpang.order.exception.CancelNotFoundException
import kea.dpang.order.exception.UnableToCancelException
import kea.dpang.order.feign.dto.ItemInfoDto
import kea.dpang.order.feign.dto.UpdateStockListRequestDto
import kea.dpang.order.feign.dto.UpdateStockRequestDto
import kea.dpang.order.feign.dto.UserDto
import kea.dpang.order.repository.CancelRepository
import kea.dpang.order.repository.OrderRepository
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class CancelServiceImpl(
    private val itemService: ItemService,
    private val mileageService: MileageService,
    private val userService: UserService,
    orderRepository: OrderRepository,
    private val cancelRepository: CancelRepository
) : CancelService, BaseService(itemService, userService, orderRepository) {

    private val log = LoggerFactory.getLogger(CancelServiceImpl::class.java)

    override fun cancelOrder(orderId: Long, orderDetailId: Long) {
        log.info("주문 취소 시작. 주문 ID: {}, 주문 상세 ID: {}", orderId, orderDetailId)

        // 주문 상세 정보를 조회한다.
        val orderDetail = fetchOrderDetail(orderId, orderDetailId)

        // 조회된 주문 상태를 확인하여, 주문이 취소가 가능한지 확인한다.
        checkCancelAvailable(orderDetail)

        // 주문 상태를 '취소'로 변경한다.
        orderDetail.status = CANCELLED

        // 주문 취소 정보
        val cancel = Cancel(
            orderDetail = orderDetail,
            refundAmount = orderDetail.order.productPaymentAmount + orderDetail.order.deliveryFee
        )

        // 취소 정보를 데이터베이스에 저장한다.
        cancelRepository.save(cancel)
        log.info("주문 취소 완료. 주문 상세 ID: {}", orderDetailId)

        // 주문 상세 정보에 취소 정보를 연관 관계 편의 메서드를 사용하여 추가한다.
        orderDetail.assignCancel(cancel)

        // 취소된 주문에 포함된 상품의 개수를 상품 서비스에 요청하여 재고를 증가시킨다.
        itemService.updateStockInfo(
            UpdateStockListRequestDto(
                listOf(
                    UpdateStockRequestDto(
                        itemId = orderDetail.itemId,
                        quantity = orderDetail.quantity
                    )
                )
            )
        )

        // 주문에 사용된 마일리지를 마일리지 서비스에 요청하여 사용자에게 환불한다.
        mileageService.refundMileage(orderDetail.order.userId, cancel.refundAmount, "주문 취소")

        log.info("주문 취소 완료. 주문 상세 ID: {}", orderDetailId)
    }

    /**
     * 주문 상세 정보의 상태를 확인하여 주문 취소가 가능한지 확인하는 메서드
     * 주문 상태가 '결제 완료'인 경우에만 취소가 가능하다.
     *
     * @param orderDetail 주문 상세 정보
     */
    private fun checkCancelAvailable(orderDetail: OrderDetail) {
        if (orderDetail.status != PAYMENT_COMPLETED) {
            log.error("주문 취소 불가능 상태. 주문 상세 ID: {}", orderDetail.id)
            throw UnableToCancelException()
        }
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

    @Transactional(readOnly = true)
    override fun getCancelList(
        startDate: LocalDate?,
        endDate: LocalDate?,
        userId: Long?,
        pageable: Pageable
    ): Page<CancelDto> {
        log.info("취소 목록 조회 시작. 시작 날짜: {}, 종료 날짜: {}, 사용자 ID: {}, 페이지 정보: {}", startDate, endDate, userId, pageable)

        val cancels = cancelRepository
            .findCancels(startDate, endDate, userId, pageable)

        log.info("취소 목록 조회 완료. 조회된 취소 건수: {}", cancels.totalElements)

        // 취소 목록이 비어있는 경우 빈 페이지를 반환한다.
        if (cancels.isEmpty) {
            return Page.empty()
        }

        // 취소 목록에 포함된 상품 ID와 사용자 ID를 추출한다.
        val itemIds = cancels.map { it.orderDetail.itemId }.distinct()
        val userIds = cancels.map { it.orderDetail.order.userId }.distinct()

        log.info("취소 목록에 포함된 상품 ID 목록: {}, 사용자 ID 목록: {}", itemIds, userIds)

        // runBlocking 블록 내에서 비동기로 상품 정보 조회와 사용자 정보 조회한다.
        val (items, users) = runBlocking { fetchItemAndUserInfos(itemIds, userIds) }

        // RefundDto 목록으로 변환하여 반환한다.
        return cancels.map { convertCancelEntityToDto(it, users, items) }
    }

    /**
     * Cancel 엔티티를 CancelDto로 변환하는 메서드
     *
     * @param cancel 변환할 Cancel 엔티티 객체
     * @param users 사용자 정보 목록
     * @param items 상품 정보 목록
     * @return 변환된 CancelDto 객체
     */
    private fun convertCancelEntityToDto(
        cancel: Cancel,
        users: Map<Long, UserDto>? = null,
        items: Map<Long, ItemInfoDto>? = null
    ): CancelDto {
        val orderDetail = cancel.orderDetail

        // 상품 정보가 넘어오지 않았다면 상품 서비스에 요청하여 상품 정보를 얻는다.
        val product = items?.get(orderDetail.itemId) ?: itemService.getItemInfo(orderDetail.itemId)

        // 사용자 정보가 넘어오지 않았다면 사용자 서비스에 요청하여 사용자 정보를 얻는다.
        val user = users?.get(orderDetail.order.userId) ?: userService.getUserInfo(orderDetail.order.userId)

        return CancelDto(cancel, user.name, ProductInfoDto.from(product))
    }

}
