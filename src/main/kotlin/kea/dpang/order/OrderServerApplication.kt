package kea.dpang.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OrderServerApplication

fun main(args: Array<String>) {
    runApplication<OrderServerApplication>(*args)
}
