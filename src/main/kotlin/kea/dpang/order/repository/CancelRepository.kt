package kea.dpang.order.repository

import kea.dpang.order.entity.Cancel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CancelRepository: JpaRepository<Cancel, Long>, CancelRepositoryCustom {
}
