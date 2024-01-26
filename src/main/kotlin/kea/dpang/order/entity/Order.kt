package kea.dpang.order.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

/**
 * 주문 정보를 나타내는 엔티티 클래스
 *
 * @property id 주문 번호.
 * @property userId 사용자 ID
 * @property deliveryRequest 배송시 요청사항
 * @property productPaymentAmount 상품 결제 금액
 * @property deliveryFee 배송비
 * @property date 주문 시각.
 * @property updatedAt 주문 정보 업데이트 시각.
 * @property updateRecipient 주문의 수령자 정보
 * @property details 주문에 속한 주문 상세 리스트
 */
@Entity(name = "orders")
data class Order(

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null, // 주문 번호

    @Column(name = "user_id")
    val userId: Long, // 사용자 ID

    @Column(name = "delivery_request")
    var deliveryRequest: String, // 배송시 요청사항

    @Column(name = "product_payment_amount")
    var productPaymentAmount: Int, // 상품 결제 금액

    @Column(name = "delivery_fee")
    var deliveryFee: Int = 3_000, // 배송비. 기본값은 3,000원

    @CreationTimestamp
    @Column(name = "order_date")
    var date: LocalDateTime? = null, // 생성 날짜

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null, // 변경 날짜

    @OneToOne(mappedBy = "order", cascade = [CascadeType.ALL])
    var recipient: OrderRecipient? = null,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var details: MutableList<OrderDetail> = mutableListOf()

) {

    /**
     * 주문의 수령인 정보를 업데이트하는 메소드
     *
     * @param recipient 새로 갱신할 [OrderRecipient] 객체.
     */
    fun updateRecipient(recipient: OrderRecipient) {
        recipient.order = this // 주문 수령인에 현재 주문을 설정
        this.recipient = recipient // 현재 주문에 주문 수령인을 설정
    }
}