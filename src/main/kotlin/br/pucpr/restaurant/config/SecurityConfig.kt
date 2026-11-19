package br.pucpr.restaurant.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }                                  // API stateless
            .headers { h -> h.frameOptions { it.sameOrigin() } }    // necessário para o H2 console
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.DELETE, "/**").authenticated()
                    .anyRequest().permitAll()
            }
            .httpBasic(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(
        encoder: PasswordEncoder,
        @Value("\${app.security.username:admin}") username: String,
        @Value("\${app.security.password:admin123}") password: String
    ): UserDetailsService {
        val user = User.withUsername(username)
            .password(encoder.encode(password))
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(user)
    }
}
