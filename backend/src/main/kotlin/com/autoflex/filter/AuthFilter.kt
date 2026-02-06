package com.autoflex.filter

import jakarta.annotation.Priority
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider

@Provider
@Priority(jakarta.ws.rs.Priorities.AUTHENTICATION)
class AuthFilter : ContainerRequestFilter {

    private val protectedPaths = listOf(
        "/swagger-ui",
        "/q/openapi",
        "/q/health",
        "/health"
    )

    private val validPassword = "projedata"

    override fun filter(requestContext: ContainerRequestContext) {
        val path = requestContext.uriInfo.path
        val requestUri = requestContext.uriInfo.requestUri.path

        // Verificar se a rota está na lista de rotas protegidas
        val isProtected = protectedPaths.any { protectedPath ->
            path.startsWith(protectedPath) || 
            requestUri.startsWith(protectedPath) ||
            path.contains(protectedPath) || 
            requestUri.contains(protectedPath)
        }

        if (!isProtected) {
            // Rota não protegida, permitir acesso
            return
        }

        // Verificar autenticação via header X-API-Key ou query parameter apiKey
        val apiKey = requestContext.getHeaderString("X-API-Key")
            ?: requestContext.uriInfo.queryParameters.getFirst("apiKey")

        if (apiKey == null || apiKey != validPassword) {
            // Senha inválida ou não fornecida
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(
                        mapOf(
                            "error" to "Unauthorized",
                            "message" to "Acesso negado. Forneça a senha correta via header 'X-API-Key' ou query parameter 'apiKey'."
                        )
                    )
                    .build()
            )
        }
        // Se a senha estiver correta, a requisição continua normalmente
    }
}
