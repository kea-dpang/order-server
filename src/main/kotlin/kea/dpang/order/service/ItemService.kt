package kea.dpang.order.service

import kea.dpang.order.feign.dto.ItemInfoDto
import kea.dpang.order.feign.dto.UpdateStockListRequestDto

interface ItemService {

    fun getItemInfo(itemId: Long): ItemInfoDto

    fun getItemInfos(itemIds: List<Long>): List<ItemInfoDto>

    fun updateStockInfo(dto: UpdateStockListRequestDto)
}