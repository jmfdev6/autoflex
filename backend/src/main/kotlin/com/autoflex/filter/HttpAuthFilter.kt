package com.autoflex.filter

import io.quarkus.vertx.http.runtime.filters.Filters
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty

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
    
    // Verificar se a requisição está autenticada
    private fun isAuthenticated(ctx: RoutingContext): Boolean {
        // Tentar header primeiro
        val headerKey = ctx.request().getHeader("X-API-Key")
        if (headerKey != null && headerKey == validPassword) {
            return true
        }
        
        // Tentar cookie (útil para requisições após redirect)
        val cookie = ctx.request().getCookie("swagger-api-key")
        if (cookie != null && cookie.value == validPassword) {
            return true
        }
        
        // Obter URI completo e extrair query string
        val uri = ctx.request().uri()
        val queryStart = uri.indexOf('?')
        if (queryStart != -1) {
            val queryString = uri.substring(queryStart + 1)
            // Parse manual da query string
            val params = queryString.split("&").associate {
                val parts = it.split("=", limit = 2)
                val key = parts[0]
                val value = if (parts.size > 1) {
                    try {
                        java.net.URLDecoder.decode(parts[1], "UTF-8")
                    } catch (e: Exception) {
                        parts[1]
                    }
                } else {
                    ""
                }
                key to value
            }
            
            val apiKey = params["apiKey"] 
                ?: params["apikey"]
                ?: params["API_KEY"]
                ?: params["ApiKey"]
            
            if (apiKey != null && apiKey == validPassword) {
                return true
            }
        }
        
        // Tentar extrair apiKey do Referer (útil quando browser segue redirect)
        val referer = ctx.request().getHeader("Referer")
        if (referer != null && referer.contains("/swagger-ui")) {
            val refererQueryStart = referer.indexOf('?')
            if (refererQueryStart != -1) {
                val refererQuery = referer.substring(refererQueryStart + 1)
                // Remover fragmento se existir
                val refererQueryClean = refererQuery.split("#")[0]
                val refererParams = refererQueryClean.split("&").associate {
                    val parts = it.split("=", limit = 2)
                    val key = parts[0]
                    val value = if (parts.size > 1) {
                        try {
                            java.net.URLDecoder.decode(parts[1], "UTF-8")
                        } catch (e: Exception) {
                            parts[1]
                        }
                    } else {
                        ""
                    }
                    key to value
                }
                
                val refererApiKey = refererParams["apiKey"] 
                    ?: refererParams["apikey"]
                    ?: refererParams["API_KEY"]
                    ?: refererParams["ApiKey"]
                
                if (refererApiKey != null && refererApiKey == validPassword) {
                    return true
                }
            }
        }
        
        // Fallback para métodos do Vert.x
        val apiKey = ctx.request().getParam("apiKey")
            ?: ctx.request().getParam("apikey")
            ?: ctx.request().getParam("API_KEY")
            ?: ctx.request().getParam("ApiKey")
        
        return apiKey != null && apiKey == validPassword
    }

    @ConfigProperty(name = "autoflex.api.key", defaultValue = "projedata")
    lateinit var validPassword: String

    fun register(@Observes filters: Filters) {
        filters.register(io.vertx.core.Handler { ctx: RoutingContext ->
            val path = ctx.request().path()
            val referer = ctx.request().getHeader("Referer") ?: ""
            
            // Normalizar path para verificação (remover barra final)
            val normalizedPath = if (path.length > 1 && path.endsWith("/")) {
                path.dropLast(1)
            } else {
                path
            }
            
            // Verificar se é rota do Swagger UI (principal ou com barra final)
            val isSwaggerUI = normalizedPath == "/swagger-ui"
            // Recursos estáticos são paths que começam com /swagger-ui/ mas não são exatamente /swagger-ui/
            val isSwaggerResource = path.startsWith("/swagger-ui/") && normalizedPath != "/swagger-ui"
            
            // Verificar se a rota está protegida
            val isProtected = protectedPaths.any { protectedPath ->
                normalizedPath.startsWith(protectedPath) || path.startsWith(protectedPath)
            }
            
            // Para rota principal do Swagger UI (com ou sem barra final)
            if (isSwaggerUI) {
                if (!isAuthenticated(ctx)) {
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
                
                // Se autenticado e tem apiKey na query, definir cookie para requisições subsequentes
                val uri = ctx.request().uri()
                val queryStart = uri.indexOf('?')
                if (queryStart != -1) {
                    val queryString = uri.substring(queryStart + 1)
                    val params = queryString.split("&").associate {
                        val parts = it.split("=", limit = 2)
                        parts[0] to (if (parts.size > 1) {
                            try {
                                java.net.URLDecoder.decode(parts[1], "UTF-8")
                            } catch (e: Exception) {
                                parts[1]
                            }
                        } else "")
                    }
                    val apiKey = params["apiKey"] 
                        ?: params["apikey"]
                        ?: params["API_KEY"]
                        ?: params["ApiKey"]
                    
                    if (apiKey != null && apiKey == validPassword) {
                        // Definir cookie com apiKey (válido por 1 hora)
                        ctx.response().addCookie(
                            io.vertx.core.http.Cookie.cookie("swagger-api-key", apiKey)
                                .setPath("/")
                                .setMaxAge(3600)
                                .setHttpOnly(false) // Permitir acesso via JavaScript se necessário
                        )
                    }
                }
                
                ctx.next()
                return@Handler
            }
            
            // Para recursos estáticos do Swagger UI: permitir se autenticado ou se veio do Swagger UI (com apiKey válido)
            if (isSwaggerResource) {
                val isFromSwagger = referer.contains("/swagger-ui")
                // Se veio do Swagger UI, verificar se tem apiKey válido no Referer
                if (isFromSwagger && referer.contains("apiKey=")) {
                    // Extrair apiKey do Referer
                    val refererQueryStart = referer.indexOf('?')
                    if (refererQueryStart != -1) {
                        val refererQuery = referer.substring(refererQueryStart + 1).split("#")[0]
                        val refererParams = refererQuery.split("&").associate {
                            val parts = it.split("=", limit = 2)
                            parts[0] to (if (parts.size > 1) {
                                try {
                                    java.net.URLDecoder.decode(parts[1], "UTF-8")
                                } catch (e: Exception) {
                                    parts[1]
                                }
                            } else "")
                        }
                        val refererApiKey = refererParams["apiKey"] 
                            ?: refererParams["apikey"]
                            ?: refererParams["API_KEY"]
                            ?: refererParams["ApiKey"]
                        if (refererApiKey != null && refererApiKey == validPassword) {
                            ctx.next()
                            return@Handler
                        }
                    }
                }
                // Se autenticado na requisição atual, permitir
                if (isAuthenticated(ctx)) {
                    ctx.next()
                    return@Handler
                }
                // Se não autenticado e não veio do Swagger, bloquear
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
            
            // Para outras rotas protegidas (/q/openapi, /health, etc)
            if (isProtected) {
                // Para /q/openapi.json: só permitir se vier do Swagger UI (via Referer)
                // Bloquear acesso direto mesmo com senha (por segurança)
                if (normalizedPath.startsWith("/q/openapi") || path.startsWith("/q/openapi")) {
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
                
                // Para outras rotas protegidas (/health, etc), verificar autenticação
                if (!isAuthenticated(ctx)) {
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
