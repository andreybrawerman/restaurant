package br.pucpr.restaurant.repositories

import br.pucpr.restaurant.models.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: String): Role?
}
