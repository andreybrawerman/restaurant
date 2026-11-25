package br.pucpr.restaurant.models

import jakarta.persistence.*

@Entity
@Table(name = "tbl_roles")
class Role(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var description: String = ""
) {
    // Dois papéis são iguais se tiverem o mesmo nome (o nome é único)
    override fun equals(other: Any?): Boolean = this === other || (other is Role && name == other.name)

    override fun hashCode(): Int = name.hashCode()
}

/** Nomes dos papéis da aplicação. No Spring Security viram as authorities ROLE_ADMIN e ROLE_USER. */
object Roles {
    const val ADMIN = "ADMIN"
    const val USER = "USER"
}
