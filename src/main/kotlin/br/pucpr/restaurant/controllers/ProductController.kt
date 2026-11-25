package br.pucpr.restaurant.controllers

import br.pucpr.restaurant.dtos.ProductRequest
import br.pucpr.restaurant.dtos.ProductResponse
import br.pucpr.restaurant.services.ProductService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(private val service: ProductService) {

    @PostMapping
    fun create(@Valid @RequestBody request: ProductRequest): ResponseEntity<ProductResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    @GetMapping
    fun list(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) minPrice: Double?,
        @RequestParam(required = false) maxPrice: Double?,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "asc") direction: String
    ): ResponseEntity<List<ProductResponse>> =
        ResponseEntity.ok(service.search(name, categoryId, minPrice, maxPrice, sortBy, direction))

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<ProductResponse> =
        ResponseEntity.ok(service.get(id))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: ProductRequest): ResponseEntity<ProductResponse> =
        ResponseEntity.ok(service.update(id, request))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
