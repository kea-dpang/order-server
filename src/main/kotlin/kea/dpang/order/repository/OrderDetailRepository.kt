package kea.dpang.order.repository

import kea.dpang.order.entity.OrderDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderDetailRepository: JpaRepository<OrderDetail, Long> {
}
