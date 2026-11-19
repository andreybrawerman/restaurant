package br.pucpr.restaurant.exceptions

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException): ResponseEntity<ApiError> {
        log.warn("Recurso não encontrado: {}", ex.message)
        return build(HttpStatus.NOT_FOUND, ex.message ?: "Recurso não encontrado")
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ApiError> {
        log.warn("Conflito: {}", ex.message)
        return build(HttpStatus.CONFLICT, ex.message ?: "Conflito")
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ApiError> {
        log.warn("Requisição inválida: {}", ex.message)
        return build(HttpStatus.BAD_REQUEST, ex.message ?: "Requisição inválida")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val details = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        log.warn("Falha de validação: {}", details)
        return build(HttpStatus.BAD_REQUEST, "Dados inválidos", details)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(ex: HttpMessageNotReadableException): ResponseEntity<ApiError> {
        log.warn("Corpo da requisição ilegível: {}", ex.message)
        return build(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou mal formatado")
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiError> {
        log.warn("Parâmetro com tipo inválido: {}={}", ex.name, ex.value)
        return build(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '${ex.name}': ${ex.value}")
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleIntegrity(ex: DataIntegrityViolationException): ResponseEntity<ApiError> {
        log.warn("Violação de integridade: {}", ex.mostSpecificCause.message)
        return build(HttpStatus.CONFLICT, "Operação viola a integridade dos dados (registro duplicado ou em uso)")
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ApiError> {
        if (ex is ErrorResponse) {
            val status = HttpStatus.resolve(ex.statusCode.value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
            log.warn("Erro HTTP {}: {}", status.value(), ex.message)
            return build(status, ex.message ?: status.reasonPhrase)
        }
        log.error("Erro inesperado", ex)
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor")
    }

    private fun build(status: HttpStatus, message: String, details: List<String> = emptyList()) =
        ResponseEntity.status(status).body(
            ApiError(status = status.value(), error = status.reasonPhrase, message = message, details = details)
        )
}
