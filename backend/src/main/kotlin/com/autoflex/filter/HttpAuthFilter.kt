package com.autoflex.filter

import io.quarkus.vertx.http.runtime.filters.Filters
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes

@ApplicationScoped
class HttpAuthFilter {

    // Rotas protegidas
    // /swagger-ui precisa de senha para acessar
    // /q/openapi.json não precisa de senha quando vem do Swagger UI (via Referer)
    private val protectedPaths = listOf(
        "/swagger-ui",
        "/q/openapi",
        "/q/health",
        "/health"
    )

    private val validPassword = "projedata"

    fun register(@Observes filters: Filters) {
        filters.register(io.vertx.core.Handler { ctx: RoutingContext ->
            val path = ctx.request().path()
            val userAgent = ctx.request().getHeader("User-Agent") ?: ""
            val referer = ctx.request().getHeader("Referer") ?: ""
            
            // Verificar se a rota está na lista de rotas protegidas
            val isProtected = protectedPaths.any { protectedPath ->
                path.startsWith(protectedPath)
            }

            if (isProtected) {
                // Para /q/openapi.json: só permitir se vier do Swagger UI (via Referer)
                // Bloquear acesso direto mesmo com senha (por segurança)
                if (path.startsWith("/q/openapi")) {
                    val isFromSwagger = referer.contains("/swagger-ui")
                    
                    if (!isFromSwagger) {
                        // Bloquear acesso direto ao OpenAPI JSON (mesmo com senha)
                        ctx.response()
                            .setStatusCode(403)
                            .putHeader("Content-Type", "application/json")
                            .end(
                                """
                                {
                                    "error": "Forbidden",
                                    "message": "Acesso direto ao OpenAPI JSON não é permitido por motivos de segurança. Use o Swagger UI."
                                }
                                """.trimIndent()
                            )
                        return@Handler
                    }
                    // Se vier do Swagger UI, permitir sem senha
                    ctx.next()
                    return@Handler
                }
                
                // Para outras rotas protegidas (/swagger-ui, /health, etc), verificar autenticação
                val apiKey = ctx.request().getHeader("X-API-Key")
                    ?: ctx.request().getParam("apiKey")

                if (apiKey == null || apiKey != validPassword) {
                    // Senha inválida ou não fornecida
                    ctx.response()
                        .setStatusCode(401)
                        .putHeader("Content-Type", "application/json")
                        .end(
                            """
                            {
                                "error": "Unauthorized",
                                "message": "Acesso negado. Forneça a senha correta via header 'X-API-Key' ou query parameter 'apiKey'."
                            }
                            """.trimIndent()
                        )
                    return@Handler
                }
            }
            
            // Continuar com a requisição
            ctx.next()
        }, 100) // Prioridade 100 (alta prioridade)
    }
}
