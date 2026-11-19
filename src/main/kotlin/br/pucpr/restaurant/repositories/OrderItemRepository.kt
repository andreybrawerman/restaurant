package br.pucpr.restaurant.repositories

import br.pucpr.restaurant.models.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Long> {
    fun existsByProductId(productId: Long): Boolean
}
