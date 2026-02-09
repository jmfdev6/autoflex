package com.autoflex.service

import com.autoflex.dto.AuthRequest
import com.autoflex.dto.RefreshTokenRequest
import com.autoflex.exception.UnauthorizedException
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

@QuarkusTest
class AuthServiceTest {
    
    @Inject
    lateinit var authService: AuthService
    
    @BeforeEach
    fun setUp() {
        // Clean up any existing refresh tokens
        // Note: In a real implementation, this would be done via a cleanup method
    }
    
    @Test
    fun `should authenticate with valid credentials`() {
        val request = AuthRequest(
            username = "admin",
            password = "admin123"
        )
        
        val response = authService.authenticate(request)
        
        assertNotNull(response.accessToken)
        assertNotNull(response.refreshToken)
        assertEquals("Bearer", response.tokenType)
        assertTrue(response.expiresIn > 0)
        assertTrue(response.accessToken.isNotBlank())
        assertTrue(response.refreshToken.isNotBlank())
    }
    
    @Test
    fun `should throw UnauthorizedException with invalid credentials`() {
        val request = AuthRequest(
            username = "invalid",
            password = "invalid"
        )
        
        assertThrows(UnauthorizedException::class.java) {
            authService.authenticate(request)
        }
    }
    
    @Test
    fun `should throw UnauthorizedException with wrong password`() {
        val request = AuthRequest(
            username = "admin",
            password = "wrongpassword"
        )
        
        assertThrows(UnauthorizedException::class.java) {
            authService.authenticate(request)
        }
    }
    
    @Test
    fun `should refresh token with valid refresh token`() {
        // First authenticate
        val authRequest = AuthRequest(
            username = "admin",
            password = "admin123"
        )
        val authResponse = authService.authenticate(authRequest)
        
        // Then refresh
        val refreshRequest = RefreshTokenRequest(
            refreshToken = authResponse.refreshToken
        )
        
        val refreshResponse = authService.refreshToken(refreshRequest)
        
        assertNotNull(refreshResponse.accessToken)
        assertNotNull(refreshResponse.refreshToken)
        assertNotEquals(authResponse.accessToken, refreshResponse.accessToken)
        assertNotEquals(authResponse.refreshToken, refreshResponse.refreshToken)
    }
    
    @Test
    fun `should throw UnauthorizedException with invalid refresh token`() {
        val refreshRequest = RefreshTokenRequest(
            refreshToken = "invalid-token"
        )
        
        assertThrows(UnauthorizedException::class.java) {
            authService.refreshToken(refreshRequest)
        }
    }
    
    @Test
    fun `should revoke refresh token`() {
        // First authenticate
        val authRequest = AuthRequest(
            username = "admin",
            password = "admin123"
        )
        val authResponse = authService.authenticate(authRequest)
        
        // Revoke token
        authService.revokeToken(authResponse.refreshToken)
        
        // Try to refresh with revoked token
        val refreshRequest = RefreshTokenRequest(
            refreshToken = authResponse.refreshToken
        )
        
        assertThrows(UnauthorizedException::class.java) {
            authService.refreshToken(refreshRequest)
        }
    }
}
