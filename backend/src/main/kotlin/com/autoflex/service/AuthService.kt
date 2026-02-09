package com.autoflex.service

import com.autoflex.dto.AuthRequest
import com.autoflex.dto.AuthResponse
import com.autoflex.dto.RefreshTokenRequest
import com.autoflex.exception.BadRequestException
import com.autoflex.exception.UnauthorizedException
import io.smallrye.jwt.build.Jwt
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Service for handling authentication and JWT token generation.
 * 
 * This service provides:
 * - User authentication (login)
 * - JWT access token generation
 * - Refresh token generation and validation
 * - Token refresh functionality
 */
@ApplicationScoped
class AuthService {
    
    @ConfigProperty(name = "autoflex.jwt.issuer", defaultValue = "autoflex-backend")
    lateinit var jwtIssuer: String
    
    @ConfigProperty(name = "autoflex.jwt.access-token.duration", defaultValue = "3600")
    var accessTokenDuration: Long = 3600 // 1 hour in seconds
    
    @ConfigProperty(name = "autoflex.jwt.refresh-token.duration", defaultValue = "86400")
    var refreshTokenDuration: Long = 86400 // 24 hours in seconds
    
    @ConfigProperty(name = "autoflex.auth.admin.username", defaultValue = "admin")
    lateinit var adminUsername: String
    
    @ConfigProperty(name = "autoflex.auth.admin.password", defaultValue = "admin123")
    lateinit var adminPassword: String
    
    // In-memory storage for refresh tokens (in production, use Redis or database)
    private val refreshTokens = mutableMapOf<String, RefreshTokenInfo>()
    
    data class RefreshTokenInfo(
        val username: String,
        val roles: Set<String>,
        val expiresAt: Instant
    )
    
    /**
     * Authenticates a user and returns JWT tokens.
     * 
     * @param request Authentication request with username and password
     * @return AuthResponse containing access and refresh tokens
     * @throws UnauthorizedException if credentials are invalid
     */
    fun authenticate(request: AuthRequest): AuthResponse {
        // TODO: Replace with proper user database lookup
        // For now, using simple admin credentials
        if (request.username != adminUsername || request.password != adminPassword) {
            throw UnauthorizedException("Invalid username or password")
        }
        
        val roles = setOf("user", "admin")
        val accessToken = generateAccessToken(request.username, roles)
        val refreshToken = generateRefreshToken(request.username, roles)
        
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = accessTokenDuration
        )
    }
    
    /**
     * Refreshes an access token using a valid refresh token.
     * 
     * @param request Refresh token request
     * @return AuthResponse with new access and refresh tokens
     * @throws UnauthorizedException if refresh token is invalid or expired
     */
    fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        val tokenInfo = refreshTokens[request.refreshToken]
            ?: throw UnauthorizedException("Invalid refresh token")
        
        // Check if refresh token is expired
        if (tokenInfo.expiresAt.isBefore(Instant.now())) {
            refreshTokens.remove(request.refreshToken)
            throw UnauthorizedException("Refresh token has expired")
        }
        
        // Generate new tokens
        val accessToken = generateAccessToken(tokenInfo.username, tokenInfo.roles)
        val newRefreshToken = generateRefreshToken(tokenInfo.username, tokenInfo.roles)
        
        // Remove old refresh token and store new one
        refreshTokens.remove(request.refreshToken)
        refreshTokens[newRefreshToken] = RefreshTokenInfo(
            username = tokenInfo.username,
            roles = tokenInfo.roles,
            expiresAt = Instant.now().plus(refreshTokenDuration, ChronoUnit.SECONDS)
        )
        
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = newRefreshToken,
            expiresIn = accessTokenDuration
        )
    }
    
    /**
     * Revokes a refresh token (logout).
     */
    fun revokeToken(refreshToken: String) {
        refreshTokens.remove(refreshToken)
    }
    
    /**
     * Generates a JWT access token.
     */
    private fun generateAccessToken(username: String, roles: Set<String>): String {
        val now = Instant.now()
        return Jwt.issuer(jwtIssuer)
            .subject(username)
            .groups(roles)
            .claim("type", "access")
            .issuedAt(now)
            .expiresAt(now.plus(accessTokenDuration, ChronoUnit.SECONDS))
            .sign()
    }
    
    /**
     * Generates a refresh token and stores it for validation.
     */
    private fun generateRefreshToken(username: String, roles: Set<String>): String {
        val token = UUID.randomUUID().toString()
        val expiresAt = Instant.now().plus(refreshTokenDuration, ChronoUnit.SECONDS)
        
        refreshTokens[token] = RefreshTokenInfo(
            username = username,
            roles = roles,
            expiresAt = expiresAt
        )
        
        return token
    }
}
