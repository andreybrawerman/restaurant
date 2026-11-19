package br.pucpr.restaurant.dtos

import br.pucpr.restaurant.models.Category
import jakarta.validation.constraints.NotBlank

data class CategoryRequest(
    @field:NotBlank(message = "O nome da categoria é obrigatório")
    val name: String = ""
)

data class CategoryResponse(val id: Long, val name: String)

fun Category.toResponse() = CategoryResponse(id!!, name)
