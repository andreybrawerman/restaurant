package br.pucpr.restaurant.security

/** Usuário identificado pelo token JWT (o "principal" da requisição). */
data class UserToken(val id: Long, val name: String, val roles: Set<String>)
