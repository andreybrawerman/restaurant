package br.pucpr.restaurant.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "tbl_categories")
class Category(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank
    @Column(unique = true)
    var name: String = "",

    // one-to-many: uma categoria tem vários produtos
    @OneToMany(mappedBy = "category")
    var products: MutableList<Product> = mutableListOf()
)
