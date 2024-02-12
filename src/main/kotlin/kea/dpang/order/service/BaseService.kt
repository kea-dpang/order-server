package kea.dpang.order.service

import kea.dpang.order.feign.dto.ItemInfoDto
import kea.dpang.order.feign.dto.UserDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

abstract class BaseService(
    private val itemService: ItemService,
    private val userService: UserService
) {
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
