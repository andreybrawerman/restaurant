package br.pucpr.restaurant.controllers

import br.pucpr.restaurant.dtos.LoginRequest
import br.pucpr.restaurant.dtos.LoginResponse
import br.pucpr.restaurant.dtos.RegisterRequest
import br.pucpr.restaurant.dtos.UserResponse
import br.pucpr.restaurant.exceptions.UnauthorizedException
import br.pucpr.restaurant.security.UserToken
import br.pucpr.restaurant.security.currentUser
import br.pucpr.restaurant.services.UserService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val service: UserService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.register(request))

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<LoginResponse> =
        ResponseEntity.ok(service.login(request))

    /** Mostra quem é o usuário identificado pelo token enviado. */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    fun me(): ResponseEntity<UserToken> =
        ResponseEntity.ok(currentUser() ?: throw UnauthorizedException("Autenticação necessária"))
}
