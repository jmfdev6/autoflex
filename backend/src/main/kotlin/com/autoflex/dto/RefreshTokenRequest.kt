package com.autoflex.dto

import jakarta.validation.constraints.NotBlank

/**
 * Request DTO for refreshing access token.
 */
data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String
)
