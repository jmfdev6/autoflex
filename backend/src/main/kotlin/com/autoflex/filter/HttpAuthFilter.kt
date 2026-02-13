package com.autoflex.filter

import io.quarkus.vertx.http.runtime.filters.Filters
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import org.eclipse.microprofile.config.Config
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class HttpAuthFilter {

    @Inject
    lateinit var config: Config

    // Rotas protegidas
    // /swagger-ui precisa de senha para acessar
    // /q/openapi.json não precisa de senha quando vem do Swagger UI (via Referer)
    // Health check (/health, /api/v1/health, /q/health) é público para probes e load balancers
    private val protectedPaths = listOf(
        "/swagger-ui",
        "/q/openapi"
    )
    
    private fun getAllowedOrigins(): List<String> {
        // Primeiro tenta ler da variável de ambiente CORS_ORIGINS
        val envOrigins = System.getenv("CORS_ORIGINS")
        if (envOrigins != null && envOrigins.isNotBlank()) {
            return envOrigins.split(",").map { it.trim().removeSuffix("/") }
        }
        
        // Depois tenta ler da configuração do Quarkus
        // O Quarkus resolve automaticamente o perfil (%prod, %dev, etc)
        val configOrigins = try {
            // Verificar se config está inicializado antes de acessá-lo
            if (!::config.isInitialized) {
                // Config não está inicializado, usar fallback
                return listOf("https://*.vercel.app")
            }
            
            // Tentar ler a propriedade diretamente (o Quarkus já resolve o perfil)
            val value = config.getOptionalValue("quarkus.http.cors.origins", String::class.java)
            if (value.isPresent) {
                value.get()
            } else {
                // Fallback: usar origem de produção padrão
                "https://*.vercel.app"
            }
        } catch (e: kotlin.UninitializedPropertyAccessException) {
            "https://*.vercel.app"
        } catch (e: Exception) {
            "https://*.vercel.app"
        }
        
        return configOrigins.split(",").map { it.trim().removeSuffix("/") }
    }
    
    private fun isOriginAllowed(origin: String?, allowedOrigins: List<String>): Boolean {
        if (origin == null) return false
        if (allowedOrigins.contains("*")) return true
        
        // Normalizar origem removendo barra final para comparação
        val normalizedOrigin = origin.trim().removeSuffix("/")
        
        // Verificar correspondência exata
        if (allowedOrigins.any { it.equals(normalizedOrigin, ignoreCase = true) }) {
            return true
        }
        
        // Verificar padrões wildcard (ex: https://*.vercel.app)
        return allowedOrigins.any { pattern ->
            if (pattern.contains("*")) {
                val regex = pattern
                    .replace(".", "\\.")
                    .replace("*", ".*")
                    .toRegex(RegexOption.IGNORE_CASE)
                regex.matches(normalizedOrigin)
            } else {
                false
            }
        }
    }
    
    private fun addCorsHeaders(ctx: RoutingContext, origin: String?, allowedOrigins: List<String>) {
        // SEMPRE adicionar Access-Control-Allow-Origin se houver origem na requisição
        // Este filtro executa primeiro (prioridade 10), então não há risco de duplicação aqui
        if (origin != null) {
            if (isOriginAllowed(origin, allowedOrigins)) {
                if (allowedOrigins.contains("*")) {
                    ctx.response().putHeader("Access-Control-Allow-Origin", "*")
                } else {
                    ctx.response().putHeader("Access-Control-Allow-Origin", origin)
                    ctx.response().putHeader("Access-Control-Allow-Credentials", "true")
                }
            } else {
                // Mesmo que não seja permitida, adicionar para evitar erro "header not present"
                // O browser vai bloquear de qualquer forma, mas pelo menos não vai dar erro de header ausente
                ctx.response().putHeader("Access-Control-Allow-Origin", origin)
            }
        } else {
            // Se não há origem, permitir todas (para requisições do mesmo domínio)
            ctx.response().putHeader("Access-Control-Allow-Origin", "*")
        }
        
        // Sempre adicionar outros headers CORS
        ctx.response().putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
        ctx.response().putHeader("Access-Control-Allow-Headers", "accept, authorization, content-type, x-requested-with, access-control-request-method, access-control-request-headers, origin")
        ctx.response().putHeader("Access-Control-Expose-Headers", "*")
    }
    
    // Verificar se a requisição está autenticada
    private fun isAuthenticated(ctx: RoutingContext): Boolean {
        try {
            // Verificar se validPassword está inicializado
            if (!::validPassword.isInitialized) {
                return false
            }
        } catch (e: kotlin.UninitializedPropertyAccessException) {
            return false
        } catch (e: Exception) {
            return false
        }
        
        try {
            // Tentar header primeiro
            val headerKey = ctx.request().getHeader("X-API-Key")
            if (headerKey != null && ::validPassword.isInitialized && headerKey == validPassword) {
                return true
            }
            
            // Tentar cookie (útil para requisições após redirect)
            try {
                val cookie = ctx.request().getCookie("swagger-api-key")
                if (cookie != null && ::validPassword.isInitialized && cookie.value == validPassword) {
                    return true
                }
            } catch (e: Exception) {
                // Ignorar erro ao ler cookie
            }
            
            // Obter URI completo e extrair query string
            try {
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
                    
                    if (apiKey != null && ::validPassword.isInitialized && apiKey == validPassword) {
                        return true
                    }
                }
            } catch (e: Exception) {
                // Ignorar erro ao processar query string
            }
            
            // Tentar extrair apiKey do Referer (útil quando browser segue redirect)
            try {
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
                        
                        if (refererApiKey != null && ::validPassword.isInitialized && refererApiKey == validPassword) {
                            return true
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignorar erro ao processar Referer
            }
            
            // Fallback para métodos do Vert.x
            try {
                val apiKey = ctx.request().getParam("apiKey")
                    ?: ctx.request().getParam("apikey")
                    ?: ctx.request().getParam("API_KEY")
                    ?: ctx.request().getParam("ApiKey")
                
                if (apiKey != null && ::validPassword.isInitialized && apiKey == validPassword) {
                    return true
                }
            } catch (e: Exception) {
                // Ignorar erro ao ler parâmetros
            }
        } catch (e: Exception) {
            // Em caso de qualquer erro, retornar false (não autenticado)
            return false
        }
        
        return false
    }

    @ConfigProperty(name = "autoflex.api.key", defaultValue = "projedata")
    lateinit var validPassword: String

    fun register(@Observes filters: Filters) {
        filters.register(io.vertx.core.Handler { ctx: RoutingContext ->
            try {
                val method = ctx.request().method().name()
                val origin = ctx.request().getHeader("Origin")
                
                // Processar requisições OPTIONS (preflight) IMEDIATAMENTE, antes de qualquer outra coisa
                if (method.equals("OPTIONS", ignoreCase = true)) {
                    try {
                        // SEMPRE adicionar Access-Control-Allow-Origin primeiro
                        val response = ctx.response()
                        val allowedOrigins = getAllowedOrigins()
                        
                        if (origin != null && origin.isNotBlank()) {
                            // Verificar se a origem é permitida
                            if (isOriginAllowed(origin, allowedOrigins)) {
                                response.putHeader("Access-Control-Allow-Origin", origin)
                                response.putHeader("Access-Control-Allow-Credentials", "true")
                            } else {
                                // Mesmo que não seja permitida, adicionar para evitar erro "header not present"
                                response.putHeader("Access-Control-Allow-Origin", origin)
                            }
                        } else {
                            response.putHeader("Access-Control-Allow-Origin", "*")
                        }
                        
                        // Adicionar outros headers CORS obrigatórios
                        response.putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
                        response.putHeader("Access-Control-Allow-Headers", "accept, authorization, content-type, x-requested-with, access-control-request-method, access-control-request-headers, origin")
                        response.putHeader("Access-Control-Max-Age", "86400")
                        
                        // Responder com sucesso para preflight
                        response.setStatusCode(200).end()
                        return@Handler
                    } catch (e: Exception) {
                        // Em caso de erro no preflight, ainda responder com headers básicos
                        val response = ctx.response()
                        response.putHeader("Access-Control-Allow-Origin", origin ?: "*")
                        response.putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
                        response.putHeader("Access-Control-Allow-Headers", "accept, authorization, content-type, x-requested-with, access-control-request-method, access-control-request-headers, origin")
                        response.setStatusCode(200).end()
                        return@Handler
                    }
                }
                
                // Para outras requisições, adicionar headers CORS também
                // O HttpAuthFilter gerencia TODOS os headers CORS para evitar duplicação
                try {
                    val allowedOrigins = getAllowedOrigins()
                    addCorsHeaders(ctx, origin, allowedOrigins)
                } catch (e: Exception) {
                    // Em caso de erro ao adicionar headers CORS, adicionar headers básicos
                    if (origin != null) {
                        ctx.response().putHeader("Access-Control-Allow-Origin", origin)
                    } else {
                        ctx.response().putHeader("Access-Control-Allow-Origin", "*")
                    }
                }
            } catch (e: kotlin.UninitializedPropertyAccessException) {
                // Config ou validPassword não está inicializado, usar fallback seguro
                val originError = ctx.request().getHeader("Origin")
                // Adicionar headers CORS básicos mesmo em caso de erro
                if (originError != null && originError.isNotBlank()) {
                    ctx.response().putHeader("Access-Control-Allow-Origin", originError)
                    ctx.response().putHeader("Access-Control-Allow-Credentials", "true")
                } else {
                    ctx.response().putHeader("Access-Control-Allow-Origin", "*")
                }
                ctx.response().putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
                ctx.response().putHeader("Access-Control-Allow-Headers", "accept, authorization, content-type, x-requested-with, access-control-request-method, access-control-request-headers, origin")
                // Continuar com o processamento normal mesmo em caso de erro
                ctx.next()
                return@Handler
            } catch (e: Exception) {
                // Em caso de qualquer outro erro, ainda adicionar headers CORS básicos
                val originError = ctx.request().getHeader("Origin")
                try {
                    val allowedOriginsError = getAllowedOrigins()
                    addCorsHeaders(ctx, originError, allowedOriginsError)
                } catch (ex: Exception) {
                    // Se getAllowedOrigins também falhar, usar fallback mínimo
                    if (originError != null && originError.isNotBlank()) {
                        ctx.response().putHeader("Access-Control-Allow-Origin", originError)
                        ctx.response().putHeader("Access-Control-Allow-Credentials", "true")
                    } else {
                        ctx.response().putHeader("Access-Control-Allow-Origin", "*")
                    }
                    ctx.response().putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
                    ctx.response().putHeader("Access-Control-Allow-Headers", "accept, authorization, content-type, x-requested-with, access-control-request-method, access-control-request-headers, origin")
                }
                // Continuar com o processamento normal mesmo em caso de erro
                ctx.next()
                return@Handler
            }
            
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
                try {
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
                    try {
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
                            
                            if (apiKey != null && ::validPassword.isInitialized && apiKey == validPassword) {
                                // Definir cookie com apiKey (válido por 1 hora)
                                try {
                                    ctx.response().addCookie(
                                        io.vertx.core.http.Cookie.cookie("swagger-api-key", apiKey)
                                            .setPath("/")
                                            .setMaxAge(3600)
                                            .setHttpOnly(false) // Permitir acesso via JavaScript se necessário
                                    )
                                } catch (e: Exception) {
                                    // Ignorar erro ao adicionar cookie, continuar normalmente
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Ignorar erro ao processar query string, continuar normalmente
                    }
                    
                    ctx.next()
                    return@Handler
                } catch (e: Exception) {
                    // Em caso de erro, retornar erro 500 com mensagem
                    ctx.response()
                        .setStatusCode(500)
                        .putHeader("Content-Type", "application/json")
                        .end(
                            """
                            {
                                "error": "Internal Server Error",
                                "message": "An error occurred while processing the request: ${e.message}"
                            }
                            """.trimIndent()
                        )
                    return@Handler
                }
            }
            
            // Para recursos estáticos do Swagger UI: permitir se autenticado ou se veio do Swagger UI (com apiKey válido)
            if (isSwaggerResource) {
                try {
                    val isFromSwagger = referer.contains("/swagger-ui")
                    // Se veio do Swagger UI, verificar se tem apiKey válido no Referer
                    if (isFromSwagger && referer.contains("apiKey=")) {
                        try {
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
                                if (refererApiKey != null && ::validPassword.isInitialized && refererApiKey == validPassword) {
                                    ctx.next()
                                    return@Handler
                                }
                            }
                        } catch (e: Exception) {
                            // Ignorar erro ao processar Referer, continuar com outras verificações
                        }
                    }
                    // Se autenticado na requisição atual, permitir
                    if (isAuthenticated(ctx)) {
                        ctx.next()
                        return@Handler
                    }
                } catch (e: Exception) {
                    // Em caso de erro, ainda tentar verificar autenticação básica
                    try {
                        if (isAuthenticated(ctx)) {
                            ctx.next()
                            return@Handler
                        }
                    } catch (ex: Exception) {
                        // Se tudo falhar, negar acesso
                    }
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
        }, 10) // Prioridade 10 (muito alta - executa antes de quase tudo)
    }
}
