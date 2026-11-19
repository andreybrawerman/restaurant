package br.pucpr.restaurant.exceptions

import java.time.LocalDateTime

data class ApiError(
    val timestamp: String = LocalDateTime.now().toString(),
    val status: Int,
    val error: String,
    val message: String,
    val details: List<String> = emptyList()
)
