package kea.dpang.order.dto.order

/**
 * 배송지 정보를 담는 DTO
 *
 * @property userName 사용자 이름
 * @property userPhoneNumber 사용자 전화번호
 * @property userPostalCode 사용자 우편번호
 * @property userAddress 사용자 주소
 * @property userDetailAddress 사용자 상세 주소
 */
data class DeliveryInfo(
    val userName: String,
    val userPhoneNumber: String,
    val userPostalCode: String,
    val userAddress: String,
    val userDetailAddress: String
)