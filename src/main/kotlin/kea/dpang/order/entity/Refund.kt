package kea.dpang.order.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime


/**
 * 환불 정보를 나타내는 엔티티 클래스
 *
 * @property id 환불 ID
 * @property orderDetail 주문 상세 정보
 * @property refundReason 환불 사유
 * @property note 비고
 * @property refundRequestDate 환불 요청 날짜
 * @property refundCompleteDate 환불 완료 날짜
 * @property refundAmount 환불 예정액
 * @property refundStatus 환불 상태
 * @property recall 회수 정보
 */
@Entity(name = "order_refunds")
data class Refund(

    @Id
    @Column(name = "refund_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null, // 환불 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id")
    var orderDetail: OrderDetail, // 주문 상세 ID

    @Column(name = "refund_reason")
    @Enumerated(EnumType.STRING)
    var refundReason: Reason, // 환불 사유

    @Column(name = "note")
    var note: String?, // 비고

    @CreationTimestamp
    @Column(name = "refund_request_date")
    var refundRequestDate: LocalDateTime? = null, // 환불 요청 날짜

    @Column(name = "refund_complete_date")
    var refundCompleteDate: LocalDateTime? = null, // 환불 완료 날짜

    @Column(name = "refund_amount")
    var refundAmount: Int = 0, // 환불 예정액

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status")
    var refundStatus: RefundStatus, // 환불 상태

    @OneToOne(mappedBy = "refund", cascade = [CascadeType.ALL])
    var recall: Recall? = null // 회수 정보

) {
    /**
     * 환불 정보에 회수 정보를 연결하는 편의 메소드
     *
     * @param recall 연결할 회수 정보 엔티티.
     */
    fun assignRecall(recall: Recall) {
        this.recall = recall
        recall.refund = this
    }
}