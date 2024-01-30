package kea.dpang.order.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import kea.dpang.order.entity.OrderStatus
import kea.dpang.order.exception.InvalidOrderStatusException

@Component
class StringToOrderStatusConverter : Converter<String, OrderStatus> {

    override fun convert(source: String): OrderStatus {
        return try {
            OrderStatus.valueOf(source.uppercase())
        } catch (e: IllegalArgumentException) {
            throw InvalidOrderStatusException(source)
        }
    }
}
