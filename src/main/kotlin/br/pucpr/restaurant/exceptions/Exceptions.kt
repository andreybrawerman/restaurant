package br.pucpr.restaurant.exceptions

/** 404 - Exception para recurso não encontrado */
class ResourceNotFoundException(message: String) : RuntimeException(message)

/** 409 - Exception para conflito com o estado atual (produto já usado em pedido) */
class ConflictException(message: String) : RuntimeException(message)

/** 400 - Exception para requisição inválida por regra de negócio */
class BadRequestException(message: String) : RuntimeException(message)
