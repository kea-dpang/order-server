package kea.dpang.order.repository

import kea.dpang.order.entity.Refund
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefundRepository: JpaRepository<Refund, Long>, RefundRepositoryCustom {
}
