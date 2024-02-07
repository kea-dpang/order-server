package kea.dpang.order.service

import kea.dpang.order.feign.ItemServiceFeignClient
import kea.dpang.order.feign.dto.ItemInfoDto
import kea.dpang.order.feign.dto.UpdateStockListRequestDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ItemServiceImpl(
    private val itemServiceFeignClient: ItemServiceFeignClient
) : ItemService {

    private val log = LoggerFactory.getLogger(ItemServiceImpl::class.java)

    override fun getItemInfo(itemId: Long): ItemInfoDto {
        log.info("상품 정보 조회 시작. 상품 ID: {}", itemId)
        val itemInfo = itemServiceFeignClient.getItemInfo(itemId).body!!.data
        log.info("상품 정보 조회 완료.")

        return itemInfo
    }

    override fun getItemInfos(itemIds: List<Long>): List<ItemInfoDto> {
        log.info("상품 정보 조회 시작. 상품 ID 목록: {}", itemIds)
        val itemInfos = itemServiceFeignClient.getItemInfos(itemIds).body!!.data
        log.info("상품 정보 조회 완료.")

        return itemInfos
    }

    override fun updateStockInfo(dto: UpdateStockListRequestDto) {
        log.info("상품 재고 정보 업데이트 시작. 요청: {}", dto)
        itemServiceFeignClient.updateStock(dto)
        log.info("상품 재고 정보 업데이트 완료.")
    }
}