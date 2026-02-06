package com.autoflex.resource

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test

@QuarkusTest
class ProductionResourceTest {
    
    @Test
    fun `should get production suggestions`() {
        given()
            .`when`().get("/api/production/suggestions")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data", notNullValue())
            .body("data.suggestions", notNullValue())
            .body("data.totalValue", notNullValue())
    }
    
    @Test
    fun `should return suggestions with products and raw materials`() {
        // Create raw material
        val rawMaterialBody = """
            {
                "name": "Test Raw Material",
                "stockQuantity": 100.00
            }
        """.trimIndent()
        
        val rmCode = given()
            .contentType(ContentType.JSON)
            .body(rawMaterialBody)
            .`when`().post("/api/raw-materials")
            .then()
            .extract().path<String>("data.code")
        
        // Create product
        val productBody = """
            {
                "name": "Test Product",
                "value": 50.00
            }
        """.trimIndent()
        
        val productCode = given()
            .contentType(ContentType.JSON)
            .body(productBody)
            .`when`().post("/api/products")
            .then()
            .extract().path<String>("data.code")
        
        // Create association
        val associationBody = """
            {
                "rawMaterialCode": "$rmCode",
                "quantity": 10.00
            }
        """.trimIndent()
        
        given()
            .contentType(ContentType.JSON)
            .body(associationBody)
            .`when`().post("/api/products/$productCode/raw-materials")
            .then()
            .statusCode(201)
        
        // Get suggestions
        given()
            .`when`().get("/api/production/suggestions")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.suggestions", notNullValue())
    }
}
