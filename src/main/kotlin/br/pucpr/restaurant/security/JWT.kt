package br.pucpr.restaurant.security

import br.pucpr.restaurant.models.Roles
import br.pucpr.restaurant.models.User
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.Date

/** Cria e lê os tokens JWT (assinados com HMAC-SHA256 usando o segredo configurado). */
@Component
class JWT(private val properties: SecurityProperties) {
    private val log = LoggerFactory.getLogger(JWT::class.java)

    private val key = Keys.hmacShaKeyFor(properties.secret.toByteArray())

    /** O token do ADMIN dura menos que o dos demais, por ser mais sensível. */
    fun createToken(user: User): String {
        val isAdmin = user.roles.any { it.name == Roles.ADMIN }
        val hours = if (isAdmin) properties.adminExpireHours else properties.expireHours
        val now = Date()
        return Jwts.builder()
            .issuer(properties.issuer)
            .subject(user.id.toString())
            .issuedAt(now)
            .expiration(Date(now.time + hours * 60 * 60 * 1000))
            .claim(
                "user",
                mapOf("id" to user.id, "name" to user.name, "roles" to user.roles.map { it.name }.sorted())
            )
            .signWith(key)
            .compact()
    }

    /** Devolve a autenticação do token, ou null se ele for inválido, adulterado ou estiver expirado. */
    fun extract(token: String): Authentication? =
        try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(properties.issuer)
                .build()
                .parseSignedClaims(token)
                .payload
            val user = claims["user"] as? Map<*, *>
            val id = (user?.get("id") as? Number)?.toLong()
            val name = user?.get("name")?.toString()
            if (id == null || name == null) {
                null
            } else {
                val roles = (user["roles"] as? List<*>)?.map { it.toString() }?.toSet() ?: emptySet()
                UsernamePasswordAuthenticationToken(
                    UserToken(id, name, roles),
                    null,
                    roles.map { SimpleGrantedAuthority("ROLE_$it") }
                )
            }
        } catch (e: JwtException) {
            log.debug("Token rejeitado: {}", e.message)
            null
        } catch (e: IllegalArgumentException) {
            log.debug("Token inválido: {}", e.message)
            null
        }
}
