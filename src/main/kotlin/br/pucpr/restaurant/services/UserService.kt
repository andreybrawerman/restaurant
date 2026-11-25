package br.pucpr.restaurant.services

import br.pucpr.restaurant.dtos.LoginRequest
import br.pucpr.restaurant.dtos.LoginResponse
import br.pucpr.restaurant.dtos.RegisterRequest
import br.pucpr.restaurant.dtos.UserResponse
import br.pucpr.restaurant.dtos.toResponse
import br.pucpr.restaurant.exceptions.ConflictException
import br.pucpr.restaurant.exceptions.UnauthorizedException
import br.pucpr.restaurant.models.Roles
import br.pucpr.restaurant.models.User
import br.pucpr.restaurant.repositories.RoleRepository
import br.pucpr.restaurant.repositories.UserRepository
import br.pucpr.restaurant.security.JWT
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val repository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwt: JWT
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    /** Todo usuário cadastrado pela API nasce com o papel USER. O ADMIN é criado na inicialização. */
    @Transactional
    fun register(request: RegisterRequest): UserResponse {
        val email = request.email.trim().lowercase()
        if (repository.existsByEmail(email)) {
            throw ConflictException("Já existe um usuário com o e-mail '$email'")
        }
        val role = checkNotNull(roleRepository.findByName(Roles.USER)) { "Papel ${Roles.USER} não cadastrado" }
        val user = User(
            name = request.name.trim(),
            email = email,
            password = requireNotNull(passwordEncoder.encode(request.password)),
            roles = mutableSetOf(role)
        )
        val saved = repository.save(user)
        log.info("Usuário criado: id={}, email={}", saved.id, saved.email)
        return saved.toResponse()
    }

    /** Valida e-mail e senha e devolve o token JWT. A mensagem é a mesma para os dois erros, de propósito. */
    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val user = repository.findByEmail(request.email.trim().lowercase())
        if (user == null || !passwordEncoder.matches(request.password, user.password)) {
            log.warn("Falha de login para o e-mail {}", request.email)
            throw UnauthorizedException("E-mail ou senha inválidos")
        }
        log.info("Login realizado: id={}", user.id)
        return LoginResponse(jwt.createToken(user), user.toResponse())
    }
}
