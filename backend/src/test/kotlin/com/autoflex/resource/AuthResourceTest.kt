package com.autoflex.resource

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.Test

@QuarkusTest
class AuthResourceTest {
    
    @Test
    fun `should authenticate user successfully`() {
        val requestBody = """
            {
                "username": "admin",
                "password": "admin123"
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .`when`().post("/api/auth/login")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.accessToken", notNullValue())
            .body("data.refreshToken", notNullValue())
            .body("data.tokenType", equalTo("Bearer"))
            .body("data.expiresIn", greaterThan(0))
    }
    
    @Test
    fun `should return 401 with invalid credentials`() {
        val requestBody = """
            {
                "username": "invalid",
                "password": "invalid"
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .`when`().post("/api/auth/login")
            .then()
            .statusCode(401)
            .body("success", equalTo(false))
            .body("code", equalTo("UNAUTHORIZED"))
    }
    
    @Test
    fun `should return 400 with missing fields`() {
        val requestBody = """
            {
                "username": "admin"
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .`when`().post("/api/auth/login")
            .then()
            .statusCode(400)
    }
    
    @Test
    fun `should refresh token successfully`() {
        // First authenticate
        val loginBody = """
            {
                "username": "admin",
                "password": "admin123"
            }
        """.trimIndent()
        
        val refreshToken = given()
            .contentType(ContentType.JSON)
            .body(loginBody)
            .`when`().post("/api/auth/login")
            .then()
            .extract().path<String>("data.refreshToken")
        
        // Then refresh
        val refreshBody = """
            {
                "refreshToken": "$refreshToken"
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(refreshBody)
            .`when`().post("/api/auth/refresh")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.accessToken", notNullValue())
            .body("data.refreshToken", notNullValue())
    }
    
    @Test
    fun `should logout successfully`() {
        // First authenticate
        val loginBody = """
            {
                "username": "admin",
                "password": "admin123"
            }
        """.trimIndent()
        
        val refreshToken = given()
            .contentType(ContentType.JSON)
            .body(loginBody)
            .`when`().post("/api/auth/login")
            .then()
            .extract().path<String>("data.refreshToken")
        
        // Then logout
        val logoutBody = """
            {
                "refreshToken": "$refreshToken"
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(logoutBody)
            .`when`().post("/api/auth/logout")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
    }
}
