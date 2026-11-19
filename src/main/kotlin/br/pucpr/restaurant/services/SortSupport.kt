package br.pucpr.restaurant.services

import br.pucpr.restaurant.exceptions.BadRequestException
import org.springframework.data.domain.Sort

fun buildSort(sortBy: String, direction: String, allowedFields: Set<String>): Sort {
    if (sortBy !in allowedFields) {
        throw BadRequestException("Campo de ordenação inválido: '$sortBy'. Use um destes: ${allowedFields.joinToString()}")
    }
    val dir = when (direction.lowercase()) {
        "asc" -> Sort.Direction.ASC
        "desc" -> Sort.Direction.DESC
        else -> throw BadRequestException("Direção inválida: '$direction'. Use 'asc' ou 'desc'")
    }
    return Sort.by(dir, sortBy)
}
