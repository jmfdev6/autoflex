package com.autoflex.filter

import jakarta.annotation.Priority
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider

@Provider
@Priority(jakarta.ws.rs.Priorities.AUTHENTICATION - 1)
class CorsFilter : ContainerRequestFilter, ContainerResponseFilter {
    
    override fun filter(requestContext: ContainerRequestContext) {
        // Se for uma requisição OPTIONS (preflight), responder imediatamente
        if (requestContext.method == "OPTIONS") {
            val origin = requestContext.getHeaderString("Origin")
            val allowedOrigins = listOf(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:8080"
            )
            
            // Se não há Origin, permitir (pode ser requisição do mesmo domínio)
            // Se há Origin, verificar se está na lista permitida
            val response = if (origin == null || allowedOrigins.contains(origin)) {
                val responseBuilder = Response.ok()
                
                if (origin != null) {
                    responseBuilder.header("Access-Control-Allow-Origin", origin)
                    responseBuilder.header("Access-Control-Allow-Credentials", "true")
                } else {
                    responseBuilder.header("Access-Control-Allow-Origin", "*")
                }
                
                responseBuilder
                    .header(
                        "Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, PATCH"
                    )
                    .header(
                        "Access-Control-Allow-Headers",
                        "accept, authorization, content-type, x-requested-with, origin"
                    )
                    .header("Access-Control-Max-Age", "3600")
                    .build()
            } else {
                Response.status(Response.Status.FORBIDDEN).build()
            }
            
            requestContext.abortWith(response)
        }
    }
    
    override fun filter(
        requestContext: ContainerRequestContext,
        responseContext: ContainerResponseContext
    ) {
        // O Quarkus já adiciona os headers CORS automaticamente
        // Este filtro apenas garante que requisições OPTIONS sejam tratadas corretamente
        // Não precisamos adicionar headers aqui para evitar duplicação
    }
}
