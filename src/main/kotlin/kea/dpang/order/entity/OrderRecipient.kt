package kea.dpang.order.entity

import jakarta.persistence.*

/**
 * 주문 수령인 정보를 나타내는 엔티티 클래스
 *
 * @property order 주문 엔티티. 주문 ID를 이 엔티티의 기본 키로 사용한다.
 * @property receiverName 받는 사람의 이름.
 * @property receiverPhoneNumber 받는 사람의 전화번호.
 * @property receiverZipCode 받는 사람의 우편번호.
 * @property receiverAddress 받는 사람의 주소.
 * @property receiverDetailAddress 받는 사람의 상세 주소.
 */
@Entity
class OrderRecipient(

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order, // 주문 ID

    @Column(name = "receiver_name")
    var receiverName: String, // 받는 사람

    @Column(name = "receiver_phone_number")
    var receiverPhoneNumber: String, // 받는 사람 전화번호

    @Column(name = "receiver_zip_code")
    var receiverZipCode: String, // 받는 사람 우편번호

    @Column(name = "receiver_address")
    var receiverAddress: String, // 받는 사람 주소

    @Column(name = "receiver_detail_address")
    var receiverDetailAddress: String // 받는 사람 상세 주소
)
