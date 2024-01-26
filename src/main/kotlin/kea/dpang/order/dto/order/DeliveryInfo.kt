package kea.dpang.order.dto.order

import kea.dpang.order.entity.OrderRecipient

/**
 * 배송지 정보를 담는 DTO
 *
 * @property name 사용자 이름
 * @property phoneNumber 사용자 전화번호
 * @property zipCode 사용자 우편번호
 * @property address 사용자 주소
 * @property detailAddress 사용자 상세 주소
 */
data class DeliveryInfo(
    val name: String,
    val phoneNumber: String,
    val zipCode: String,
    val address: String,
    val detailAddress: String
) {
    companion object {
        fun from(recipient: OrderRecipient): DeliveryInfo {
            return DeliveryInfo(
                name = recipient.name,
                phoneNumber = recipient.phoneNumber,
                zipCode = recipient.zipCode,
                address = recipient.address,
                detailAddress = recipient.detailAddress
            )
        }
    }
}
