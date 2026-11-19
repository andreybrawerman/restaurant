package br.pucpr.restaurant.repositories

import br.pucpr.restaurant.models.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface OrderRepository : JpaRepository<Order, Long>, JpaSpecificationExecutor<Order>
