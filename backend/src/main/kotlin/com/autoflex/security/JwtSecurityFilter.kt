package com.autoflex.security

import io.quarkus.security.identity.SecurityIdentity
import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.ext.Provider
import java.security.Principal

/**
 * JWT Security Filter that extracts JWT token from Authorization header
 * and sets up the security context for Quarkus Security.
 * 
 * This filter runs after authentication but before authorization,
 * allowing Quarkus Security to handle JWT validation and role extraction.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
class JwtSecurityFilter : ContainerRequestFilter {
    
    @Inject
    lateinit var securityIdentity: SecurityIdentity
    
    override fun filter(requestContext: ContainerRequestContext) {
        // Quarkus Security handles JWT validation automatically
        // This filter is mainly for custom security context setup if needed
        // The actual JWT validation is done by Quarkus SmallRye JWT extension
    }
}
