package br.pucpr.restaurant.config

import br.pucpr.restaurant.security.JWT
import br.pucpr.restaurant.security.JwtTokenFilter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.time.LocalDateTime

/**
 * Autenticação com JWT (sem sessão). Regras:
 * - leituras, criações e atualizações, login e cadastro são públicos;
 * - toda ação DESTRUTIVA (DELETE) exige um token de usuário com o papel ADMIN
 *   (sem token: 401; token de usuário comum: 403). Os controllers repetem a regra com @PreAuthorize.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwt: JWT): SecurityFilterChain {
        http
            .csrf { it.disable() }                                  // API stateless, sem cookies
            .cors(Customizer.withDefaults())
            .headers { h -> h.frameOptions { it.sameOrigin() } }    // necessário para o H2 console
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                    .anyRequest().permitAll()
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint { _, response, _ ->
                    writeError(response, HttpStatus.UNAUTHORIZED, "Autenticação necessária: envie um token JWT válido")
                }
                ex.accessDeniedHandler { _, response, _ ->
                    writeError(response, HttpStatus.FORBIDDEN, "Acesso negado: apenas ADMIN pode executar esta ação")
                }
            }
            .addFilterBefore(JwtTokenFilter(jwt), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    /** CORS liberado para desenvolvimento (qualquer origem). Em produção, restrinja as origens. */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
        }
        return UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", config) }
    }

    /** Resposta de erro no mesmo formato do GlobalExceptionHandler (ApiError), para os erros do filtro. */
    private fun writeError(response: HttpServletResponse, status: HttpStatus, message: String) {
        response.status = status.value()
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write(
            """{"timestamp":"${LocalDateTime.now()}","status":${status.value()},""" +
                """"error":"${status.reasonPhrase}","message":"$message","details":[]}"""
        )
    }
}
