package br.pucpr.restaurant.dtos

import br.pucpr.restaurant.models.Product
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

/** Entrada do POST e do PUT de produtos (sem id, sem pedido). */
data class ProductRequest(
    @field:NotBlank(message = "O nome é obrigatório")
    val name: String = "",

    @field:NotNull(message = "O preço é obrigatório")
    @field:Positive(message = "O preço deve ser maior que zero")
    val price: Double? = null,

    val categoryId: Long? = null
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Double,
    val categoryId: Long?,
    val categoryName: String?
)

fun Product.toResponse() = ProductResponse(id!!, name, price, category?.id, category?.name)
