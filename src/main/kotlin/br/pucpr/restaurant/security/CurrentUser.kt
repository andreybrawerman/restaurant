package br.pucpr.restaurant.security

import org.springframework.security.core.context.SecurityContextHolder

/** Usuário da requisição atual, ou null se ela for anônima. */
fun currentUser(): UserToken? = SecurityContextHolder.getContext().authentication?.principal as? UserToken

/** Texto para os logs: quem executou a ação. */
fun currentUserName(): String = currentUser()?.let { "${it.name} (id=${it.id})" } ?: "anônimo"
