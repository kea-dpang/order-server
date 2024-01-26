package kea.dpang.order.base

import java.time.LocalDateTime

/**
 * BaseErrorResponse는 API 에러 응답의 기본 형식을 정의하는 클래스
 * 모든 API 에러 응답은 이 클래스를 상속받아야 하며, BaseResponse의 속성에 추가로 에러에 대한 상세 정보를 포함한다.
 *
 * @param status HTTP 상태 코드
 * @param message 에러 메시지
 * @param error 발생한 에러의 이름
 * @param path 에러가 발생한 요청 경로
 * @param timestamp 에러 발생 시간
 */
class ErrorResponse(
    override val status: Int,
    override val message: String,
    val error: String,
    val path: String,
    val timestamp: LocalDateTime
) : BaseResponse(status, message)