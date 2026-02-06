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
    val rawMaterialCode: String,
    
    @JsonProperty("quantity")
    @field:NotNull(message = "Quantity is required")
    @field:Positive(message = "Quantity must be positive")
    val quantity: BigDecimal
)

data class UpdateProductRawMaterialRequest @JsonCreator constructor(
    @JsonProperty("quantity")
    @field:NotNull(message = "Quantity is required")
    @field:Positive(message = "Quantity must be positive")
    val quantity: BigDecimal
)
