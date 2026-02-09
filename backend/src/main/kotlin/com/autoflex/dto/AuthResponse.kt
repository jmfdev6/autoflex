package com.autoflex.dto

/**
 * Response DTO for authentication containing JWT tokens.
 */
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
)
