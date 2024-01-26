package kea.dpang.order.entity

import jakarta.persistence.*

/**
 * 주문 상세 정보를 나타내는 엔티티 클래스
 *
 * @property id 주문 상세 ID
 * @property order 주문 엔티티
 * @property status 주문 및 배송 상태
 * @property itemId 상품 ID
 * @property purchasePrice 구매 금액
 * @property quantity 수량
 * @property cancel 주문의 취소 정보
 * @property refund 환불 정보
 */
@Entity(name = "order_details")
data class OrderDetail(

    @Id
    @Column(name = "order_detail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null, // 주문 상세 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order, // 주문 ID

    @Enumerated(EnumType.STRING)
    var status: OrderStatus, // 주문 및 배송 상태

    @Column(name = "item_id")
    val itemId: Long, // 상품 ID

    @Column(name = "purchase_price")
    var purchasePrice: Int, // 구매 금액

    @Column(name = "quantity")
    var quantity: Int, // 수량

    @OneToOne(mappedBy = "orderDetail", cascade = [CascadeType.ALL])
    var cancel: Cancel? = null,

    @OneToOne(mappedBy = "orderDetail", cascade = [CascadeType.ALL])
    var refund: Refund? = null
) {

    /**
     * 주문 상세 정보에 취소 정보를 할당한다.
     *
     * @param cancel 할당할 취소 정보 엔티티.
     */
    fun assignCancel(cancel: Cancel) {
        this.cancel = cancel
        cancel.orderDetail = this
    }

    /**
     * 주문 상세 정보에 환불 정보를 할당한다.
     *
     * @param refund 할당할 환불 정보 엔티티.
     */
    fun assignRefund(refund: Refund) {
        this.refund = refund
        refund.orderDetail = this
    }
}