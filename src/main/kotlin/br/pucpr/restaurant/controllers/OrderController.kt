package br.pucpr.restaurant.controllers

import br.pucpr.restaurant.dtos.CreateOrderRequest
import br.pucpr.restaurant.dtos.OrderResponse
import br.pucpr.restaurant.dtos.UpdateOrderRequest
import br.pucpr.restaurant.models.OrderStatus
import br.pucpr.restaurant.services.OrderService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(private val service: OrderService) {

    @PostMapping
    fun create(@Valid @RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    /** Ex.: GET /orders?customerName=mesa&status=ABERTO&sortBy=customerName&direction=desc */
    @GetMapping
    fun list(
        @RequestParam(required = false) customerName: String?,
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<List<OrderResponse>> =
        ResponseEntity.ok(service.search(customerName, status, sortBy, direction))

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(service.get(id))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: UpdateOrderRequest): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(service.update(id, request))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    // ---------- associação pedido <-> produto ----------

    @PostMapping("/{orderId}/products/{productId}")
    fun addProduct(
        @PathVariable orderId: Long,
        @PathVariable productId: Long,
        @RequestParam(defaultValue = "1") quantity: Int
    ): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(service.addProduct(orderId, productId, quantity))

    @DeleteMapping("/{orderId}/products/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    fun removeProduct(@PathVariable orderId: Long, @PathVariable productId: Long): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(service.removeProduct(orderId, productId))
}
