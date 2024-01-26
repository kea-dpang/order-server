package kea.dpang.order.repository

import kea.dpang.order.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: JpaRepository<Order, Long>, OrderRepositoryCustom {
}
