package br.pucpr.restaurant.services

import br.pucpr.restaurant.dtos.CreateOrderRequest
import br.pucpr.restaurant.dtos.OrderResponse
import br.pucpr.restaurant.dtos.UpdateOrderRequest
import br.pucpr.restaurant.dtos.toResponse
import br.pucpr.restaurant.exceptions.BadRequestException
import br.pucpr.restaurant.exceptions.ConflictException
import br.pucpr.restaurant.exceptions.ResourceNotFoundException
import br.pucpr.restaurant.models.Order
import br.pucpr.restaurant.models.OrderItem
import br.pucpr.restaurant.models.OrderStatus
import br.pucpr.restaurant.repositories.OrderRepository
import br.pucpr.restaurant.repositories.ProductRepository
import br.pucpr.restaurant.security.currentUserName
import jakarta.persistence.criteria.Predicate
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) {
    private val log = LoggerFactory.getLogger(OrderService::class.java)

    private val sortFields = setOf("id", "customerName", "status")

    @Transactional
    fun create(request: CreateOrderRequest): OrderResponse {
        val saved = orderRepository.save(Order(customerName = request.customerName.trim()))
        log.info("Pedido criado: id={}, cliente={}", saved.id, saved.customerName)
        return saved.toResponse()
    }

    /** Consulta com filtros (customerName, status) e ordenação (sortBy, direction). */
    @Transactional(readOnly = true)
    fun search(customerName: String?, status: OrderStatus?, sortBy: String, direction: String): List<OrderResponse> {
        val sort = buildSort(sortBy, direction, sortFields)
        val spec = Specification<Order> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            if (!customerName.isNullOrBlank()) {
                predicates.add(cb.like(cb.lower(root.get<String>("customerName")), "%${customerName.trim().lowercase()}%"))
            }
            if (status != null) {
                predicates.add(cb.equal(root.get<OrderStatus>("status"), status))
            }
            cb.and(*predicates.toTypedArray())
        }
        log.debug("Listando pedidos: customerName={}, status={}, sort={} {}", customerName, status, sortBy, direction)
        return orderRepository.findAll(spec, sort).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun get(id: Long): OrderResponse = findEntity(id).toResponse()

    @Transactional
    fun update(id: Long, request: UpdateOrderRequest): OrderResponse {
        val order = findEntity(id)
        ensureEditable(order)
        order.customerName = request.customerName.trim()
        request.status?.let { order.status = it }
        log.info("Pedido atualizado: id={}, cliente={}, status={}", id, order.customerName, order.status)
        return orderRepository.save(order).toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        val order = findEntity(id)
        orderRepository.delete(order)
        log.info("Pedido removido: id={}, por={}", id, currentUserName())
    }

    /** Associa um produto ao pedido. Se o produto já estiver no pedido, soma a quantidade. */
    @Transactional
    fun addProduct(orderId: Long, productId: Long, quantity: Int): OrderResponse {
        if (quantity <= 0) throw BadRequestException("A quantidade deve ser maior que zero")
        val order = findEntity(orderId)
        ensureEditable(order)
        val product = productRepository.findById(productId)
            .orElseThrow { ResourceNotFoundException("Produto $productId não encontrado") }

        val existing = order.items.find { it.product?.id == product.id }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            order.items.add(OrderItem(order = order, product = product, quantity = quantity))
        }
        log.info("Produto {} (x{}) adicionado ao pedido {}", productId, quantity, orderId)
        return orderRepository.save(order).toResponse()
    }

    /** Remove o produto (a linha inteira) de dentro do pedido. */
    @Transactional
    fun removeProduct(orderId: Long, productId: Long): OrderResponse {
        val order = findEntity(orderId)
        ensureEditable(order)
        val removed = order.items.removeIf { it.product?.id == productId }
        if (!removed) throw ResourceNotFoundException("Produto $productId não está no pedido $orderId")
        log.info("Produto {} removido do pedido {}, por={}", productId, orderId, currentUserName())
        return orderRepository.save(order).toResponse()
    }

    private fun findEntity(id: Long): Order =
        orderRepository.findById(id).orElseThrow { ResourceNotFoundException("Pedido $id não encontrado") }

    private fun ensureEditable(order: Order) {
        if (order.status == OrderStatus.ENTREGUE || order.status == OrderStatus.CANCELADO) {
            throw ConflictException("O pedido ${order.id} está ${order.status} e não pode mais ser alterado")
        }
    }
}
