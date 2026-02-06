package com.autoflex.filter

import io.quarkus.vertx.http.runtime.filters.Filters
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.Logger
import java.util.UUID

@ApplicationScoped
class LoggingFilter {

    private val logger = Logger.getLogger(LoggingFilter::class.java)

    fun register(@Observes filters: Filters) {
        filters.register(io.vertx.core.Handler { ctx: RoutingContext ->
            val request = ctx.request()
            val correlationId = getOrCreateCorrelationId(request)
            
            // Adicionar correlation ID ao contexto para uso em outros lugares
            ctx.put("correlationId", correlationId)
            
            // Adicionar correlation ID ao header de resposta
            ctx.response().putHeader("X-Correlation-ID", correlationId)
            
            val startTime = System.currentTimeMillis()
            val method = request.method().name()
            val path = request.path()
            val query = request.query()
            val fullPath = if (query != null) "$path?$query" else path
            val userAgent = request.getHeader("User-Agent") ?: "Unknown"
            val remoteAddress = request.remoteAddress()?.host() ?: "Unknown"
            
            // Log da requisição
            logger.infof(
                "[%s] %s %s from %s (User-Agent: %s)",
                correlationId,
                method,
                fullPath,
                remoteAddress,
                userAgent
            )
            
            // Interceptar resposta para log
            ctx.response().endHandler {
                val duration = System.currentTimeMillis() - startTime
                val statusCode = ctx.response().statusCode
                
                // Log da resposta
                logger.infof(
                    "[%s] %s %s -> %d (%d ms)",
                    correlationId,
                    method,
                    fullPath,
                    statusCode,
                    duration
                )
                
                // Log de erros (4xx, 5xx)
                if (statusCode >= 400) {
                    logger.warnf(
                        "[%s] Error response: %s %s -> %d (%d ms)",
                        correlationId,
                        method,
                        fullPath,
                        statusCode,
                        duration
                    )
                }
            }
            
            // Continuar com a requisição
            ctx.next()
        }, 200) // Prioridade menor que AuthFilter (100)
    }
    
    private fun getOrCreateCorrelationId(request: HttpServerRequest): String {
        // Tentar obter correlation ID do header
        val existingId = request.getHeader("X-Correlation-ID")
        if (existingId != null && existingId.isNotBlank()) {
            return existingId
        }
        
        // Gerar novo correlation ID
        return UUID.randomUUID().toString()
    }
}
