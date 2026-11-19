package br.pucpr.restaurant.controllers

import br.pucpr.restaurant.dtos.CategoryRequest
import br.pucpr.restaurant.dtos.CategoryResponse
import br.pucpr.restaurant.services.CategoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categories")
class CategoryController(private val service: CategoryService) {

    @PostMapping
    fun create(@Valid @RequestBody request: CategoryRequest): ResponseEntity<CategoryResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    @GetMapping
    fun list(): ResponseEntity<List<CategoryResponse>> = ResponseEntity.ok(service.list())
}
