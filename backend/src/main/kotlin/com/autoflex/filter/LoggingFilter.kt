package com.autoflex.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.vertx.http.runtime.filters.Filters
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import java.util.UUID

@ApplicationScoped
class LoggingFilter {

    @Inject
    @ConfigProperty(name = "quarkus.log.console.json", defaultValue = "false")
    lateinit var jsonLogging: String
    
    private val logger = Logger.getLogger(LoggingFilter::class.java)
    
    @Inject
    lateinit var objectMapper: ObjectMapper

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
            val authorization = request.getHeader("Authorization")?.let { 
                if (it.startsWith("Bearer ")) "Bearer ***" else "***"
            }
            
            // Log estruturado da requisição
            if (jsonLogging == "true") {
                val logData = mapOf(
                    "correlationId" to correlationId,
                    "type" to "request",
                    "method" to method,
                    "path" to fullPath,
                    "remoteAddress" to remoteAddress,
                    "userAgent" to userAgent,
                    "timestamp" to System.currentTimeMillis(),
                    "authorization" to (authorization ?: "none")
                )
                logger.info(objectMapper.writeValueAsString(logData))
            } else {
                logger.infof(
                    "[%s] %s %s from %s (User-Agent: %s)",
                    correlationId,
                    method,
                    fullPath,
                    remoteAddress,
                    userAgent
                )
            }
            
            // Interceptar resposta para log
            ctx.response().endHandler {
                val duration = System.currentTimeMillis() - startTime
                val statusCode = ctx.response().statusCode
                val responseSize = ctx.response().bytesWritten()
                
                // Log estruturado da resposta
                if (jsonLogging == "true") {
                    val logData = mutableMapOf(
                        "correlationId" to correlationId,
                        "type" to "response",
                        "method" to method,
                        "path" to fullPath,
                        "statusCode" to statusCode,
                        "durationMs" to duration,
                        "responseSizeBytes" to responseSize,
                        "timestamp" to System.currentTimeMillis()
                    )
                    
                    // Adicionar informações de erro para status >= 400
                    if (statusCode >= 400) {
                        logData["error"] = true
                        logData["level"] = if (statusCode >= 500) "error" else "warn"
                    }
                    
                    val logLevel = when {
                        statusCode >= 500 -> "error"
                        statusCode >= 400 -> "warn"
                        else -> "info"
                    }
                    
                    when (logLevel) {
                        "error" -> logger.error(objectMapper.writeValueAsString(logData))
                        "warn" -> logger.warn(objectMapper.writeValueAsString(logData))
                        else -> logger.info(objectMapper.writeValueAsString(logData))
                    }
                } else {
                    // Log da resposta (formato texto)
                    logger.infof(
                        "[%s] %s %s -> %d (%d ms, %d bytes)",
                        correlationId,
                        method,
                        fullPath,
                        statusCode,
                        duration,
                        responseSize
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
