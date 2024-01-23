package kea.dpang.order.repository

import kea.dpang.order.entity.Recall
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecallRepository: JpaRepository<Recall, Long> {
}
