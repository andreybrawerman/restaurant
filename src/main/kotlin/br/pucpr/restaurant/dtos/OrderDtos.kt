package br.pucpr.restaurant.dtos

import br.pucpr.restaurant.models.Order
import br.pucpr.restaurant.models.OrderStatus
import jakarta.validation.constraints.NotBlank

/** Entrada do POST /orders: só o nome do cliente/mesa. */
data class CreateOrderRequest(
    @field:NotBlank(message = "O nome do cliente/mesa é obrigatório")
    val customerName: String = ""
)

/** Entrada do PUT /orders/{id}. */
data class UpdateOrderRequest(
    @field:NotBlank(message = "O nome do cliente/mesa é obrigatório")
    val customerName: String = "",
    val status: OrderStatus? = null
)

data class OrderItemResponse(
    val productId: Long,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double
)

data class OrderResponse(
    val id: Long,
    val customerName: String,
    val status: OrderStatus,
    val items: List<OrderItemResponse>,
    val totalPrice: Double
)

private fun round2(value: Double) = Math.round(value * 100.0) / 100.0

fun Order.toResponse(): OrderResponse {
    val itemResponses = items.map {
        val product = it.product!!
        OrderItemResponse(
            productId = product.id!!,
            productName = product.name,
            unitPrice = product.price,
            quantity = it.quantity,
            subtotal = round2(product.price * it.quantity)
        )
    }
    return OrderResponse(
        id = id!!,
        customerName = customerName,
        status = status,
        items = itemResponses,
        totalPrice = round2(itemResponses.sumOf { it.subtotal })
    )
}
