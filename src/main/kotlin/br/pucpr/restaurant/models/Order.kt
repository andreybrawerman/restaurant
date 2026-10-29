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

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var products: MutableList<Product> = mutableListOf()
)