package kea.dpang.order.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import kea.dpang.order.entity.RefundReason
import kea.dpang.order.exception.InvalidReasonException

@Component
class StringToRefundReasonConverter : Converter<String, RefundReason> {

    override fun convert(source: String): RefundReason {
        return try {
            RefundReason.valueOf(source.uppercase())
        } catch (e: IllegalArgumentException) {
            throw InvalidReasonException(source)
        }
    }
}
