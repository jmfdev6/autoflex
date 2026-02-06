package com.autoflex.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class ProductRawMaterialDto(
    val productCode: String,
    val rawMaterialCode: String,
    val quantity: BigDecimal,
    val rawMaterialName: String? = null,
    val productName: String? = null
)

data class CreateProductRawMaterialRequest @JsonCreator constructor(
    @JsonProperty("rawMaterialCode")
    @field:NotBlank(message = "Raw material code is required")
    @field:jakarta.validation.constraints.Size(min = 1, max = 50, message = "Raw material code must be between 1 and 50 characters")
    val rawMaterialCode: String,
    
    @JsonProperty("quantity")
    @field:NotNull(message = "Quantity is required")
    @field:Positive(message = "Quantity must be positive")
    @field:jakarta.validation.constraints.DecimalMin(value = "0.01", message = "Quantity must be at least 0.01")
    val quantity: BigDecimal
)

data class UpdateProductRawMaterialRequest @JsonCreator constructor(
    @JsonProperty("quantity")
    @field:NotNull(message = "Quantity is required")
    @field:Positive(message = "Quantity must be positive")
    @field:jakarta.validation.constraints.DecimalMin(value = "0.01", message = "Quantity must be at least 0.01")
    val quantity: BigDecimal
)
