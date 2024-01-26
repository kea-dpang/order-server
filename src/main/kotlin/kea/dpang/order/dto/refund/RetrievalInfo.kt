package kea.dpang.order.dto.refund

import kea.dpang.order.entity.Recall

/**
 * 회수처 정보를 담는 클래스
 *
 * @property retrieverName 회수자의 이름
 * @property retrieverContact 회수자의 연락처
 * @property retrievalAddress 회수처의 주소
 * @property retrievalMessage 회수 메시지
 */
data class RetrievalInfo(
    val retrieverName: String, // 회수자의 이름
    val retrieverContact: String, // 회수자의 연락처
    val retrievalAddress: String, // 회수처의 주소
    val retrievalMessage: String? // 회수 메시지
) {
    companion object {
        fun from(recall: Recall): RetrievalInfo {
            return RetrievalInfo(
                retrieverName = recall.retrieverName,
                retrieverContact = recall.retrieverPhoneNumber,
                retrievalAddress = recall.retrieverAddress,
                retrievalMessage = recall.retrievalMessage
            )
        }
    }
}
