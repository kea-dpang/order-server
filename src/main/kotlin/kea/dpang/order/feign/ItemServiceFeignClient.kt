package kea.dpang.order.feign

import kea.dpang.order.base.SuccessResponse
import kea.dpang.order.feign.dto.ItemInfoDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping

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
    fun getItemInfo(@PathVariable itemId: Long): SuccessResponse<ItemInfoDto>

    /**
     * 상품의 재고 수량을 증가시키는 메서드.
     *
     * @param itemId 재고를 증가시킬 상품의 ID.
     * @param quantity 증가시킬 수량.
     * @return Boolean 재고 증가가 성공적으로 이루어졌다면 true, 그렇지 않다면 false.
     */
    @PutMapping("/api/items/{itemId}/increase/{quantity}")
    fun increaseItemStock(@PathVariable itemId: Long, @PathVariable quantity: Int): SuccessResponse<ItemInfoDto>

    /**
     * 상품의 재고 수량을 감소시키는 메서드.
     *
     * @param itemId 재고를 감소시킬 상품의 ID.
     * @param quantity 감소시킬 수량.
     * @return Boolean 재고 감소가 성공적으로 이루어졌다면 true, 그렇지 않다면 false.
     */
    @PostMapping("/api/items/{itemId}/decrease/{quantity}")
    fun decreaseItemStock(@PathVariable itemId: Long, @PathVariable quantity: Int): SuccessResponse<ItemInfoDto>

}
