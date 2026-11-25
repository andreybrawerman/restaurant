package br.pucpr.restaurant.models

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "tbl_users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank
    @field:Email
    @Column(nullable = false, unique = true)
    var email: String = "",

    // Hash BCrypt (60 caracteres), nunca a senha pura
    @Column(nullable = false, length = 72)
    var password: String = "",

    @field:NotBlank
    @Column(nullable = false)
    var name: String = "",

    // many-to-many: um usuário tem vários papéis. EAGER porque o token é montado fora de transação.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "tbl_user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()
)
