package kea.dpang.order.base

/**
 * API 응답의 기본 형식을 정의하는 클래스
 * 모든 API 응답은 이 클래스를 상속받아야 하며, 공통적으로 상태 코드와 메시지를 포함한다.
 *
 * @param status HTTP 상태 코드
 * @param message 응답 메시지
 */
open class BaseResponse(
    open val status: Int,
    open val message: String
)
