package kea.dpang.order.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import kea.dpang.order.entity.Reason
import kea.dpang.order.exception.InvalidReasonChangeException

@Component
class StringToReasonConverter : Converter<String, Reason> {

    override fun convert(source: String): Reason {
        return try {
            Reason.valueOf(source.uppercase())
        } catch (e: IllegalArgumentException) {
            throw InvalidReasonChangeException(source)
        }
    }
}
