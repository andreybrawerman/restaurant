package br.pucpr.restaurant.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "tbl_orders")
class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank
    var customerName: String = "",

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.ABERTO,

    // Itens do pedido (tabela do meio). Apagar o pedido apaga só os itens, nunca os produtos.
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<OrderItem> = mutableListOf()
)
