package com.autoflex.resource

import com.autoflex.dto.ApiResponse
import com.autoflex.dto.AuthRequest
import com.autoflex.dto.AuthResponse
import com.autoflex.dto.RefreshTokenRequest
import com.autoflex.service.AuthService
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag

/**
 * REST resource for authentication endpoints.
 * 
 * Provides:
 * - POST /api/auth/login - User authentication
 * - POST /api/auth/refresh - Refresh access token
 * - POST /api/auth/logout - Revoke refresh token
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "API for user authentication and JWT token management")
class AuthResource @Inject constructor(
    private val authService: AuthService
) {
    
    @POST
    @Path("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Autentica um usuário com username e password, retornando tokens JWT (access e refresh). Use o access token no header Authorization: Bearer <token> para requisições protegidas."
    )
    @APIResponse(
        responseCode = "200",
        description = "Authentication successful",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "401",
        description = "Invalid credentials"
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid request data"
    )
    fun login(@Valid request: AuthRequest): Response {
        val authResponse = authService.authenticate(request)
        return Response.ok(
            ApiResponse(
                success = true,
                data = authResponse,
                message = "Authentication successful"
            )
        ).build()
    }
    
    @POST
    @Path("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Refreshes an expired access token using a valid refresh token."
    )
    @APIResponse(
        responseCode = "200",
        description = "Token refreshed successfully",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "401",
        description = "Invalid or expired refresh token"
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid request data"
    )
    fun refresh(@Valid request: RefreshTokenRequest): Response {
        val authResponse = authService.refreshToken(request)
        return Response.ok(
            ApiResponse(
                success = true,
                data = authResponse,
                message = "Token refreshed successfully"
            )
        ).build()
    }
    
    @POST
    @Path("/logout")
    @Operation(
        summary = "Logout user",
        description = "Revokes a refresh token, effectively logging out the user."
    )
    @APIResponse(
        responseCode = "200",
        description = "Logout successful"
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid request data"
    )
    fun logout(@Valid request: RefreshTokenRequest): Response {
        authService.revokeToken(request.refreshToken)
        return Response.ok(
            ApiResponse(
                success = true,
                data = null,
                message = "Logout successful"
            )
        ).build()
    }
}
