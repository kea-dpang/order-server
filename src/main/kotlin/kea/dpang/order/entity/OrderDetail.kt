package kea.dpang.order.entity

import jakarta.persistence.*

/**
 * 주문 상세 정보를 나타내는 엔티티 클래스
 *
 * @property order 주문 엔티티
 * @property itemId 상품 ID
 * @property quantity 수량
 * @property orderDetailId 주문 상세 ID
 * @property cancel 주문의 취소 정보
 * @property refund 환불 정보
 */
@Entity
class OrderDetail(

    @Id
    @Column(name = "order_detail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var orderDetailId: Long, // 주문 상세 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order, // 주문 ID

    @Column(name = "item_id")
    val itemId: Long, // 상품 ID

    @Column(name = "quantity")
    var quantity: Int, // 수량

    @OneToOne(mappedBy = "orderDetail", cascade = [CascadeType.ALL])
    var cancel: Cancel? = null,

    @OneToOne(mappedBy = "orderDetail", cascade = [CascadeType.ALL])
    var refund: Refund? = null
)