package br.pucpr.restaurant.controllers

import br.pucpr.restaurant.models.Product
import br.pucpr.restaurant.repositories.ProductRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(val repository: ProductRepository) {

    @PostMapping
    fun create(@Valid @RequestBody product: Product): ResponseEntity<Product> =
        ResponseEntity.status(HttpStatus.CREATED).body(repository.save(product))

    @GetMapping
    fun listAll(): ResponseEntity<List<Product>> =
        ResponseEntity.ok(repository.findAll())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build()
        repository.deleteById(id)
        return ResponseEntity.ok().build()
    }
}