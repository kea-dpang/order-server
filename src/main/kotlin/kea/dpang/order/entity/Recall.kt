package kea.dpang.order.entity

import jakarta.persistence.*


/**
 * 회수 정보를 나타내는 엔티티 클래스
 *
 * @property refund 환불 엔티티
 * @property retrieverName 회수자 명
 * @property retrieverPhoneNumber 회수자 연락처
 * @property retrieverAddress 회수자 주소
 * @property retrievalMessage 회수 메시지
 */
@Entity(name = "order_recalls")
data class Recall(

    @Id
    @Column(name = "recall_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null, // 회수 정보 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id")
    var refund: Refund? = null, // 환불 ID

    @Column(name = "retriever_name")
    var retrieverName: String, // 회수자 명

    @Column(name = "retriever_phone_number")
    var retrieverPhoneNumber: String, // 회수자 연락처

    @Column(name = "retriever_address")
    var retrieverAddress: String, // 회수자 주소

    @Column(name = "retrieval_message")
    var retrievalMessage: String?, // 회수 메시지

)