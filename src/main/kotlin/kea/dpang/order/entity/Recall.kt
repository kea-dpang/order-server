package kea.dpang.order.entity

import jakarta.persistence.*


/**
 * 회수 정보를 나타내는 엔티티 클래스
 *
 * @property recallId 회수 ID
 * @property refund 환불 엔티티
 * @property retrieverName 회수자 명
 * @property retrieverPhoneNumber 회수자 연락처
 * @property retrieverAddress 회수자 주소
 * @property retrievalMessage 회수 메시지
 */
@Entity
class Recall {

    @Id
    @Column(name = "recall_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val recallId: Long? = null // 회수 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id")
    private var refund: Refund? = null // 환불 ID

    @Column(name = "retriever_name")
    private val retrieverName: String? = null // 회수자 명

    @Column(name = "retriever_phone_number")
    private val retrieverPhoneNumber: String? = null // 회수자 연락처

    @Column(name = "retriever_address")
    private val retrieverAddress: String? = null // 회수자 주소

    @Column(name = "retrieval_message")
    private val retrievalMessage: String? = null // 회수 메시지

}