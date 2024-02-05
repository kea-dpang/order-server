package kea.dpang.order.dto

import kea.dpang.order.feign.dto.ItemInfoDto

/**
 * 상품 정보를 담는 DTO
 *
 * @property image 상품 이미지
 * @property name 상품명
 * @property price 상품 금액
 */

data class ProductInfoDto(
    val image: String,
    val name: String,
    val price: Int
) {
    companion object {
        fun from(itemInfoDto: ItemInfoDto): ProductInfoDto {
            return ProductInfoDto(
                image = itemInfoDto.thumbnailImage,
                name = itemInfoDto.name,
                price = itemInfoDto.price
            )
        }
    }
}