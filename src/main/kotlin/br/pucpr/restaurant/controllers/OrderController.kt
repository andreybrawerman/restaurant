package br.pucpr.restaurant.controllers

import br.pucpr.restaurant.models.Order
import br.pucpr.restaurant.repositories.OrderRepository
import br.pucpr.restaurant.repositories.ProductRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(
    val orderRepository: OrderRepository,
    val productRepository: ProductRepository
) {

    @PostMapping
    fun createOrder(@Valid @RequestBody order: Order): ResponseEntity<Order> =
        ResponseEntity.status(HttpStatus.CREATED).body(orderRepository.save(order))

    @GetMapping
    fun listOrders(): ResponseEntity<List<Order>> =
        ResponseEntity.ok(orderRepository.findAll())

    @PostMapping("/{orderId}/products/{productId}")
    fun addProductToOrder(@PathVariable orderId: Long, @PathVariable productId: Long): ResponseEntity<Order> {
        val order = orderRepository.findById(orderId).orElse(null) ?: return ResponseEntity.notFound().build()
        val product = productRepository.findById(productId).orElse(null) ?: return ResponseEntity.notFound().build()

        product.order = order
        order.products.add(product)
        productRepository.save(product)

        return ResponseEntity.ok(orderRepository.save(order))
    }
}