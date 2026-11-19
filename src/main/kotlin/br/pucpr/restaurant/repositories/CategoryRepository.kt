package br.pucpr.restaurant.repositories

import br.pucpr.restaurant.models.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun existsByNameIgnoreCase(name: String): Boolean
}
