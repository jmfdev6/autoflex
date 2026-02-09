package com.autoflex.exception

import com.autoflex.dto.ErrorResponse
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import java.util.stream.Collectors

@Provider
class NotFoundExceptionMapper : ExceptionMapper<NotFoundException> {
    override fun toResponse(exception: NotFoundException): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(
                ErrorResponse(
                    message = exception.message ?: "Resource not found",
                    code = "NOT_FOUND"
                )
            )
            .build()
    }
}

@Provider
class BadRequestExceptionMapper : ExceptionMapper<BadRequestException> {
    override fun toResponse(exception: BadRequestException): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(
                ErrorResponse(
                    message = exception.message ?: "Bad request",
                    code = "BAD_REQUEST"
                )
            )
            .build()
    }
}

@Provider
class ConstraintViolationExceptionMapper : ExceptionMapper<ConstraintViolationException> {
    override fun toResponse(exception: ConstraintViolationException): Response {
        val violations = exception.constraintViolations
            .map { violation: ConstraintViolation<*> ->
                "${violation.propertyPath}: ${violation.message}"
            }
        
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(
                ErrorResponse(
                    message = "Validation failed",
                    code = "VALIDATION_ERROR",
                    details = mapOf("violations" to violations)
                )
            )
            .build()
    }
}

@Provider
class ConcurrencyExceptionMapper : ExceptionMapper<ConcurrencyException> {
    override fun toResponse(exception: ConcurrencyException): Response {
        return Response.status(Response.Status.CONFLICT)
            .entity(
                ErrorResponse(
                    message = exception.message ?: "Concurrency conflict occurred",
                    code = "CONCURRENCY_CONFLICT"
                )
            )
            .build()
    }
}

@Provider
class InsufficientStockExceptionMapper : ExceptionMapper<InsufficientStockException> {
    override fun toResponse(exception: InsufficientStockException): Response {
        val details = mutableMapOf<String, Any>()
        exception.rawMaterialCode?.let { details["rawMaterialCode"] = it }
        exception.available?.let { details["available"] = it }
        exception.requested?.let { details["requested"] = it }
        
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(
                ErrorResponse(
                    message = exception.message ?: "Insufficient stock",
                    code = "INSUFFICIENT_STOCK",
                    details = if (details.isNotEmpty()) details else null
                )
            )
            .build()
    }
}

@Provider
class UnauthorizedExceptionMapper : ExceptionMapper<UnauthorizedException> {
    override fun toResponse(exception: UnauthorizedException): Response {
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity(
                ErrorResponse(
                    message = exception.message ?: "Unauthorized",
                    code = "UNAUTHORIZED"
                )
            )
            .build()
    }
}

@Provider
class GenericExceptionMapper : ExceptionMapper<Exception> {
    override fun toResponse(exception: Exception): Response {
        // Log do erro para debug (em produção, será logado automaticamente pelo Quarkus)
        exception.printStackTrace()
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(
                ErrorResponse(
                    message = exception.message ?: "An internal error occurred",
                    code = "INTERNAL_ERROR",
                    details = mapOf(
                        "exceptionType" to exception.javaClass.simpleName,
                        "message" to (exception.message ?: "No message available")
                    )
                )
            )
            .build()
    }
}
