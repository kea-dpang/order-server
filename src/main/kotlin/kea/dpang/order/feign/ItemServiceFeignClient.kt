package kea.dpang.order.feign

import kea.dpang.order.base.BaseResponse
import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.feign.dto.ItemInfoDto
import kea.dpang.order.feign.dto.UpdateStockListRequestDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// Todo: 서비스간 JWT 토큰 인증 여부 확인 및 상품 서비스 API 확인
@FeignClient(name = "item-server")
interface ItemServiceFeignClient {

    /**
     * 상품 정보를 조회하는 메서드.
     *
     * @param itemId 조회할 상품의 ID.
     * @return ProductInfoDto 상품 정보가 담긴 DTO.
     */
    @GetMapping("/api/items/{itemId}")
    fun getItemInfo(@PathVariable itemId: Long): ResponseEntity<SuccessResponse<ItemInfoDto>>

    /**
     * 상품 목록을 조회하는 메서드.
     *
     * @param itemIds 조회할 상품의 ID 목록.
     * @return 상품 목록이 담긴 DTO.
     */
    @GetMapping("/api/items/list")
    fun getItemInfos(
        @RequestParam itemIds: List<Long>
    ): ResponseEntity<SuccessResponse<List<ItemInfoDto>>>

    /**
     * 상품의 재고를 변경하는 메서드.
     *
     * @param dto 변경할 상품의 ID와 변경할 수량이 담긴 DTO.
     * @return 요청의 처리 결과를 나타내는 응답 객체.
     */
    @PutMapping("/api/items/stock")
    fun updateStock(
        @RequestBody dto: UpdateStockListRequestDto
    ): ResponseEntity<BaseResponse>

}
