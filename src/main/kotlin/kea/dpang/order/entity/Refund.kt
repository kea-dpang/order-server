package kea.dpang.order.entity

import jakarta.persistence.*
import java.time.LocalDateTime


/**
 * 환불 정보를 나타내는 엔티티 클래스
 *
 * @property refundId 환불 ID
 * @property orderDetail 주문 상세 정보
 * @property refundReason 환불 사유
 * @property note 비고
 * @property refundRequestDate 환불 요청 날짜
 * @property refundCompleteDate 환불 완료 날짜
 * @property refundAmount 환불 예정액
 * @property refundStatus 환불 상태
 * @property recall 회수 정보
 */
@Entity
class Refund {

    @Id
    @Column(name = "refund_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val refundId: Long? = null // 환불 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id")
    private val orderDetail: OrderDetail? = null // 주문 상세 ID

    @Column(name = "refund_reason")
    @Enumerated(EnumType.STRING)
    private val refundReason: Reason? = null // 환불 사유

    @Column(name = "note")
    private val note: String? = null // 비고

    @Column(name = "refund_request_date")
    private val refundRequestDate: LocalDateTime? = null // 환불 요청 날짜

    @Column(name = "refund_complete_date")
    private val refundCompleteDate: LocalDateTime? = null // 환불 완료 날짜

    @Column(name = "refund_amount")
    private val refundAmount = 0 // 환불 예정액

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status")
    private val refundStatus: RefundStatus? = null // 환불 상태

    @OneToOne(mappedBy = "refund", cascade = [CascadeType.ALL])
    private var recall: Recall? = null // 회수 정보

}