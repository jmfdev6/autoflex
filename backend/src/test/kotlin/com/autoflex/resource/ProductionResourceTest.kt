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
            .`when`().get("/api/v1/production-suggestions")
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
            .`when`().post("/api/v1/raw-materials")
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
            .`when`().post("/api/v1/products")
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
            .`when`().post("/api/v1/products/$productCode/raw-materials")
            .then()
            .statusCode(201)

        // Get suggestions
        given()
            .`when`().get("/api/v1/production-suggestions")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.suggestions", notNullValue())
    }

    @Test
    fun `should create production and confirm`() {
        // Create raw material
        val rawMaterialBody = """
            {
                "name": "RM Confirm Test",
                "stockQuantity": 200.00
            }
        """.trimIndent()
        val rmCode = given()
            .contentType(ContentType.JSON)
            .body(rawMaterialBody)
            .`when`().post("/api/v1/raw-materials")
            .then()
            .extract().path<String>("data.code")

        // Create product
        val productBody = """{"name": "Product Confirm Test", "value": 30.00}"""
        val productCode = given()
            .contentType(ContentType.JSON)
            .body(productBody)
            .`when`().post("/api/v1/products")
            .then()
            .extract().path<String>("data.code")

        // Associate
        given()
            .contentType(ContentType.JSON)
            .body("""{"rawMaterialCode": "$rmCode", "quantity": 5.00}""")
            .`when`().post("/api/v1/products/$productCode/raw-materials")
            .then()
            .statusCode(201)

        // Create production
        val createBody = """
            {
                "items": [
                    {"productCode": "$productCode", "quantity": 2}
                ]
            }
        """.trimIndent()
        val productionId = given()
            .contentType(ContentType.JSON)
            .body(createBody)
            .`when`().post("/api/v1/productions")
            .then()
            .statusCode(201)
            .body("success", equalTo(true))
            .body("data.id", notNullValue())
            .body("data.status", equalTo("PENDING"))
            .extract().path<Long>("data.id")

        // Confirm production
        given()
            .`when`().post("/api/v1/productions/$productionId/confirm")
            .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.successCount", equalTo(1))
            .body("data.failureCount", equalTo(0))
    }
}
