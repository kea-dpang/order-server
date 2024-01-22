package kea.dpang.order.entity


import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

/**
 * 주문 정보를 나타내는 엔티티 클래스
 *
 * @property orderId 주문 번호.
 * @property userId 사용자 ID
 * @property status 주문 및 배송 상태
 * @property deliveryRequest 배송시 요청사항
 * @property paymentAmount 결제 금액
 * @property createdAt 주문 생성 시각.
 * @property updatedAt 주문 정보 업데이트 시각.
 * @property orderRecipient 주문의 수령자 정보
 * @property orderDetails 주문에 속한 주문 상세 리스트
 */
@Entity
class Order(

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var orderId: Long, // 주문 번호

    @Column(name = "user_id")
    val userId: Long, // 사용자 ID

    @Enumerated(EnumType.STRING)
    var status: OrderStatus, // 주문 및 배송 상태

    @Column(name = "delivery_request")
    var deliveryRequest: String, // 배송시 요청사항

    @Column(name = "payment_amount")
    var paymentAmount: Int, // 결제 금액

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime, // 생성 날짜

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime, // 변경 날짜

    @OneToOne(mappedBy = "order", cascade = [CascadeType.ALL])
    var orderRecipient: OrderRecipient? = null,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var orderDetails: MutableList<OrderDetail> = mutableListOf()

)