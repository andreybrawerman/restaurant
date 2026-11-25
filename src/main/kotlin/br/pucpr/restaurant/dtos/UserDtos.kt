package br.pucpr.restaurant.dtos

import br.pucpr.restaurant.models.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/** Entrada do POST /auth/register. */
data class RegisterRequest(
    @field:NotBlank(message = "O nome é obrigatório")
    val name: String = "",

    @field:NotBlank(message = "O e-mail é obrigatório")
    @field:Email(message = "E-mail inválido")
    val email: String = "",

    @field:NotBlank(message = "A senha é obrigatória")
    @field:Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    val password: String = ""
)

/** Entrada do POST /auth/login. */
data class LoginRequest(
    @field:NotBlank(message = "O e-mail é obrigatório")
    val email: String = "",

    @field:NotBlank(message = "A senha é obrigatória")
    val password: String = ""
)

data class UserResponse(val id: Long, val name: String, val email: String, val roles: List<String>)

data class LoginResponse(val token: String, val user: UserResponse)

fun User.toResponse() = UserResponse(id!!, name, email, roles.map { it.name }.sorted())
