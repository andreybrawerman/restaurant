package br.pucpr.restaurant.models

import jakarta.persistence.*
import jakarta.validation.constraints.Positive

/** Tabela do meio da relação many-to-many Order <-> Product (com a quantidade). */
@Entity
@Table(name = "tbl_order_items")
class OrderItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    var order: Order? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    var product: Product? = null,

    @field:Positive
    var quantity: Int = 1
)
