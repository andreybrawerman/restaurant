package br.pucpr.restaurant.services

import br.pucpr.restaurant.dtos.CategoryRequest
import br.pucpr.restaurant.dtos.CategoryResponse
import br.pucpr.restaurant.dtos.toResponse
import br.pucpr.restaurant.exceptions.ConflictException
import br.pucpr.restaurant.models.Category
import br.pucpr.restaurant.repositories.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(private val repository: CategoryRepository) {
    private val log = LoggerFactory.getLogger(CategoryService::class.java)

    @Transactional
    fun create(request: CategoryRequest): CategoryResponse {
        val name = request.name.trim()
        if (repository.existsByNameIgnoreCase(name)) {
            throw ConflictException("Já existe uma categoria chamada '$name'")
        }
        val saved = repository.save(Category(name = name))
        log.info("Categoria criada: id={}, nome={}", saved.id, saved.name)
        return saved.toResponse()
    }

    @Transactional(readOnly = true)
    fun list(): List<CategoryResponse> =
        repository.findAll(Sort.by("name")).map { it.toResponse() }
}
