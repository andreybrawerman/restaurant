package br.pucpr.restaurant.security

import org.springframework.boot.context.properties.ConfigurationProperties

/** Configurações de segurança, lidas do arquivo security.yaml (prefixo "security"). */
@ConfigurationProperties("security")
data class SecurityProperties(
    val issuer: String,
    val secret: String,
    val expireHours: Long,
    val adminExpireHours: Long,
    val adminName: String,
    val adminEmail: String,
    val adminPassword: String
)
