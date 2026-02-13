package com.autoflex.resource

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test

@QuarkusTest
class RawMaterialsResourceTest {
    
    @Test
    fun `should get all raw materials`() {
        given()
            .`when`().get("/api/v1/raw-materials")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data", notNullValue())
    }
    
    @Test
    fun `should create raw material`() {
        val requestBody = """
            {
                "name": "Test Raw Material",
                "stockQuantity": 100.00
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .`when`().post("/api/v1/raw-materials")
            .then()
            .statusCode(201)
            .body("success", equalTo(true))
            .body("data.code", notNullValue())
            .body("data.name", equalTo("Test Raw Material"))
            .body("data.stockQuantity", equalTo(100.00f))
    }
    
    @Test
    fun `should update raw material`() {
        // First create a raw material
        val createBody = """
            {
                "name": "Original Name",
                "stockQuantity": 100.00
            }
        """.trimIndent()
        
        val code = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .`when`().post("/api/v1/raw-materials")
            .then()
            .extract().path<String>("data.code")
        
        // Then update it
        val updateBody = """
            {
                "name": "Updated Name",
                "stockQuantity": 200.00
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
            .`when`().put("/api/v1/raw-materials/$code")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.name", equalTo("Updated Name"))
            .body("data.stockQuantity", equalTo(200.00f))
    }
    
    @Test
    fun `should delete raw material`() {
        // First create a raw material
        val createBody = """
            {
                "name": "To Delete",
                "stockQuantity": 100.00
            }
        """.trimIndent()
        
        val code = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .`when`().post("/api/v1/raw-materials")
            .then()
            .extract().path<String>("data.code")
        
        // Then delete it
        given()
            .`when`().delete("/api/v1/raw-materials/$code")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
    }
}
