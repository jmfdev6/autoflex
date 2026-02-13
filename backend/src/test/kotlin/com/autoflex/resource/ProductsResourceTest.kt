package com.autoflex.resource

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.jupiter.api.Test

@QuarkusTest
class ProductsResourceTest {
    
    @Test
    fun `should get all products paginated`() {
        given()
            .`when`().get("/api/v1/products")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.content", notNullValue())
            .body("data.page", equalTo(0))
            .body("data.size", equalTo(20))
            .body("data.totalElements", notNullValue())
            .body("data.totalPages", notNullValue())
            .body("data.first", equalTo(true))
    }
    
    @Test
    fun `should create product`() {
        val requestBody = """
            {
                "name": "Test Product",
                "value": 100.50
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .`when`().post("/api/v1/products")
            .then()
            .statusCode(201)
            .body("success", equalTo(true))
            .body("data.code", notNullValue())
            .body("data.name", equalTo("Test Product"))
            .body("data.value", equalTo(100.50f))
    }
    
    @Test
    fun `should return 400 when creating product with invalid data`() {
        val requestBody = """
            {
                "name": "",
                "value": -10
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .`when`().post("/api/v1/products")
            .then()
            .statusCode(400)
    }
    
    @Test
    fun `should get product by code`() {
        // First create a product
        val createBody = """
            {
                "name": "Test Product",
                "value": 100.50
            }
        """.trimIndent()
        
        val code = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .`when`().post("/api/v1/products")
            .then()
            .extract().path<String>("data.code")
        
        // Then get it
        given()
            .`when`().get("/api/v1/products/$code")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.code", equalTo(code))
    }
    
    @Test
    fun `should return 404 when product not found`() {
        given()
            .`when`().get("/api/v1/products/NONEXISTENT")
            .then()
            .statusCode(404)
            .body("success", equalTo(false))
    }
    
    @Test
    fun `should update product`() {
        // First create a product
        val createBody = """
            {
                "name": "Original Name",
                "value": 100.00
            }
        """.trimIndent()
        
        val code = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .`when`().post("/api/v1/products")
            .then()
            .extract().path<String>("data.code")
        
        // Then update it
        val updateBody = """
            {
                "name": "Updated Name",
                "value": 200.00
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
            .`when`().put("/api/v1/products/$code")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.name", equalTo("Updated Name"))
            .body("data.value", equalTo(200.00f))
    }
    
    @Test
    fun `should delete product`() {
        // First create a product
        val createBody = """
            {
                "name": "To Delete",
                "value": 100.00
            }
        """.trimIndent()
        
        val code = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .`when`().post("/api/v1/products")
            .then()
            .extract().path<String>("data.code")
        
        // Then delete it
        given()
            .`when`().delete("/api/v1/products/$code")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
        
        // Verify it's deleted
        given()
            .`when`().get("/api/v1/products/$code")
            .then()
            .statusCode(404)
    }
    
    @Test
    fun `should get paginated products`() {
        // Create multiple products first
        repeat(5) { index ->
            val createBody = """
                {
                    "name": "Product $index",
                    "value": ${index * 10}.00
                }
            """.trimIndent()
            
            given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .`when`().post("/api/v1/products")
                .then()
                .statusCode(201)
        }
        
        // Get paginated
        given()
            .queryParam("page", 0)
            .queryParam("size", 3)
            .queryParam("sort", "code")
            .`when`().get("/api/v1/products")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.content", notNullValue())
            .body("data.content.size()", lessThanOrEqualTo(3))
    }
    
    @Test
    fun `should return 400 when updating with invalid data`() {
        // First create a product
        val createBody = """
            {
                "name": "Test Product",
                "value": 100.00
            }
        """.trimIndent()
        
        val code = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .`when`().post("/api/v1/products")
            .then()
            .extract().path<String>("data.code")
        
        // Try to update with invalid data
        val updateBody = """
            {
                "name": "",
                "value": -10
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
            .`when`().put("/api/v1/products/$code")
            .then()
            .statusCode(400)
    }
    
    @Test
    fun `should handle partial update`() {
        // First create a product
        val createBody = """
            {
                "name": "Original Name",
                "value": 100.00
            }
        """.trimIndent()
        
        val code = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .`when`().post("/api/v1/products")
            .then()
            .extract().path<String>("data.code")
        
        // Update only name
        val updateBody = """
            {
                "name": "Updated Name"
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
            .`when`().put("/api/v1/products/$code")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.name", equalTo("Updated Name"))
            .body("data.value", equalTo(100.00f))
    }
}
