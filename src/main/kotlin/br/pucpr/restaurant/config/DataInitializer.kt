package br.pucpr.restaurant.config

import br.pucpr.restaurant.models.Role
import br.pucpr.restaurant.models.Roles
import br.pucpr.restaurant.models.User
import br.pucpr.restaurant.repositories.RoleRepository
import br.pucpr.restaurant.repositories.UserRepository
import br.pucpr.restaurant.security.SecurityProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/** Na inicialização, garante os papéis ADMIN e USER e o usuário administrador (dados do security.yaml). */
@Component
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val properties: SecurityProperties
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(args: ApplicationArguments) {
        val adminRole = roleRepository.findByName(Roles.ADMIN)
            ?: roleRepository.save(Role(name = Roles.ADMIN, description = "Administrador do sistema"))
        roleRepository.findByName(Roles.USER)
            ?: roleRepository.save(Role(name = Roles.USER, description = "Usuário comum"))

        val email = properties.adminEmail.trim().lowercase()
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(
                User(
                    name = properties.adminName,
                    email = email,
                    password = requireNotNull(passwordEncoder.encode(properties.adminPassword)),
                    roles = mutableSetOf(adminRole)
                )
            )
            log.info("Usuário administrador criado: {}", email)
        }
    }
}
