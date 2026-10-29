package br.pucpr.restaurant.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

@Entity
@Table(name = "tbl_products")
class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank
    var name: String = "",

    @field:Positive
    var price: Double = 0.0,

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    var order: Order? = null
)