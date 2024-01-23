package kea.dpang.order.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 주문 취소 정보를 나타내는 엔티티 클래스
 *
 * @property id 취소 ID
 * @property orderDetail 주문 상세 정보 엔티티
 * @property reason 취소 사유
 * @property requestDate 취소 요청 날짜
 * @property completeDate 취소 완료 날짜
 */

@Entity
class Cancel(

    @Id
    @Column(name = "cancel_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long, // 취소 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id")
    var orderDetail: OrderDetail, // 주문 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason")
    var reason: Reason, // 취소 사유

    @Column(name = "cancel_request_date")
    var requestDate: LocalDateTime, // 취소 요청 날짜

    @Column(name = "cancel_complete_date")
    var completeDate: LocalDateTime // 취소 완료 날짜
)
