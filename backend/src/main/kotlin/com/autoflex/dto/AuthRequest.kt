package com.autoflex.dto

import jakarta.validation.constraints.NotBlank

/**
 * Request DTO for authentication (login).
 */
data class AuthRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String
)
