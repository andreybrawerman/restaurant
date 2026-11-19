package br.pucpr.restaurant.services

import br.pucpr.restaurant.dtos.ProductRequest
import br.pucpr.restaurant.dtos.ProductResponse
import br.pucpr.restaurant.dtos.toResponse
import br.pucpr.restaurant.exceptions.BadRequestException
import br.pucpr.restaurant.exceptions.ConflictException
import br.pucpr.restaurant.exceptions.ResourceNotFoundException
import br.pucpr.restaurant.models.Category
import br.pucpr.restaurant.models.Product
import br.pucpr.restaurant.repositories.CategoryRepository
import br.pucpr.restaurant.repositories.OrderItemRepository
import br.pucpr.restaurant.repositories.ProductRepository
import jakarta.persistence.criteria.Predicate
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val repository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val orderItemRepository: OrderItemRepository
) {
    private val log = LoggerFactory.getLogger(ProductService::class.java)

    private val sortFields = setOf("id", "name", "price")

    @Transactional
    fun create(request: ProductRequest): ProductResponse {
        val product = Product(
            name = request.name.trim(),
            price = request.price!!,
            category = findCategory(request.categoryId)
        )
        val saved = repository.save(product)
        log.info("Produto criado: id={}, nome={}, preço={}", saved.id, saved.name, saved.price)
        return saved.toResponse()
    }

    /** Consulta com filtros (name, categoryId, minPrice, maxPrice) e ordenação (sortBy, direction). */
    @Transactional(readOnly = true)
    fun search(
        name: String?,
        categoryId: Long?,
        minPrice: Double?,
        maxPrice: Double?,
        sortBy: String,
        direction: String
    ): List<ProductResponse> {
        val sort = buildSort(sortBy, direction, sortFields)
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw BadRequestException("minPrice não pode ser maior que maxPrice")
        }
        val spec = Specification<Product> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            if (!name.isNullOrBlank()) {
                predicates.add(cb.like(cb.lower(root.get<String>("name")), "%${name.trim().lowercase()}%"))
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get<Category>("category").get<Long>("id"), categoryId))
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get<Double>("price"), minPrice))
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get<Double>("price"), maxPrice))
            }
            cb.and(*predicates.toTypedArray())
        }
        log.debug("Listando produtos: name={}, categoryId={}, min={}, max={}, sort={} {}",
            name, categoryId, minPrice, maxPrice, sortBy, direction)
        return repository.findAll(spec, sort).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun get(id: Long): ProductResponse = findEntity(id).toResponse()

    @Transactional
    fun update(id: Long, request: ProductRequest): ProductResponse {
        val product = findEntity(id)
        product.name = request.name.trim()
        product.price = request.price!!
        product.category = findCategory(request.categoryId)
        log.info("Produto atualizado: id={}, nome={}, preço={}", id, product.name, product.price)
        return repository.save(product).toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        val product = findEntity(id)
        if (orderItemRepository.existsByProductId(id)) {
            throw ConflictException("O produto $id está em um ou mais pedidos e não pode ser removido")
        }
        repository.delete(product)
        log.info("Produto removido: id={}", id)
    }

    private fun findEntity(id: Long): Product =
        repository.findById(id).orElseThrow { ResourceNotFoundException("Produto $id não encontrado") }

    private fun findCategory(categoryId: Long?): Category? =
        categoryId?.let {
            categoryRepository.findById(it).orElseThrow { ResourceNotFoundException("Categoria $it não encontrada") }
        }
}
