package kea.dpang.order.service

import kea.dpang.order.entity.Order
import kea.dpang.order.entity.OrderDetail
import kea.dpang.order.exception.OrderDetailNotFoundException
import kea.dpang.order.exception.OrderNotFoundException
import kea.dpang.order.feign.dto.ItemInfoDto
import kea.dpang.order.feign.dto.UserDto
import kea.dpang.order.repository.OrderRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory

abstract class BaseService(
    private val itemService: ItemService,
    private val userService: UserService,
    private val orderRepository: OrderRepository
) {

    private val log = LoggerFactory.getLogger(BaseService::class.java)

    /**
     * 주문 정보 조회
     *
     * @param orderId 주문 식별자
     */
    protected fun fetchOrder(orderId: Long): Order = orderRepository.findById(orderId)
        .orElseThrow {
            log.error("주문 정보를 찾을 수 없음. 주문 ID: {}", orderId)
            throw OrderNotFoundException(orderId)
        }


    /**
     * 주문 상세 정보 조회
     *
     * @param orderId 주문 식별자
     * @param orderDetailId 주문 상세 식별자
     */
    protected fun fetchOrderDetail(orderId: Long, orderDetailId: Long): OrderDetail {
        val order = fetchOrder(orderId)

        return order.details.find { it.id == orderDetailId }
            ?: throw OrderDetailNotFoundException(orderDetailId)
    }

    /**
     * 상품 및 사용자 정보 조회
     *
     * @param itemIds 상품 식별자 목록
     * @param userIds 사용자 식별자 목록
     */
    protected suspend fun fetchItemAndUserInfos(
        itemIds: List<Long>,
        userIds: List<Long>
    ): Pair<Map<Long, ItemInfoDto>, Map<Long, UserDto>> = coroutineScope {
        val itemsDeferred = async {
            itemService.getItemInfos(itemIds).associateBy { it.id }
        }

        val usersDeferred = async {
            userService.getUserInfos(userIds).associateBy { it.userId }
        }

        Pair(itemsDeferred.await(), usersDeferred.await())
    }

}
