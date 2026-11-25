package br.pucpr.restaurant.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Lê o header "Authorization: Bearer <token>" e, se o token for válido, define o usuário como logado.
 * Sem token (ou com token inválido) a requisição segue anônima; quem barra é a configuração de rotas.
 *
 * Não é um @Component de propósito: ele é criado no SecurityConfig, para rodar só dentro da cadeia do
 * Spring Security (como bean, o Spring Boot o registraria também como filtro de servlet).
 */
class JwtTokenFilter(private val jwt: JWT) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith(PREFIX)) {
            jwt.extract(header.removePrefix(PREFIX).trim())?.let {
                SecurityContextHolder.getContext().authentication = it
            }
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        private const val PREFIX = "Bearer "
    }
}
