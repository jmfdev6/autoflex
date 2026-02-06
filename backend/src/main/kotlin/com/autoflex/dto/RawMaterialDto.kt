package com.autoflex.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

data class RawMaterialDto(
    val code: String,
    val name: String,
    val stockQuantity: BigDecimal
)

data class CreateRawMaterialRequest @JsonCreator constructor(
    @JsonProperty("name")
    @field:NotBlank(message = "Raw material name is required")
    @field:jakarta.validation.constraints.Size(min = 1, max = 255, message = "Raw material name must be between 1 and 255 characters")
    val name: String,
    
    @JsonProperty("stockQuantity")
    @field:NotNull(message = "Stock quantity is required")
    @field:PositiveOrZero(message = "Stock quantity must be zero or positive")
    @field:jakarta.validation.constraints.DecimalMin(value = "0.0", message = "Stock quantity must be zero or positive")
    val stockQuantity: BigDecimal
)

data class UpdateRawMaterialRequest @JsonCreator constructor(
    @JsonProperty("name")
    @field:jakarta.validation.constraints.Size(min = 1, max = 255, message = "Raw material name must be between 1 and 255 characters")
    val name: String? = null,
    
    @JsonProperty("stockQuantity")
    @field:PositiveOrZero(message = "Stock quantity must be zero or positive")
    @field:jakarta.validation.constraints.DecimalMin(value = "0.0", message = "Stock quantity must be zero or positive")
    val stockQuantity: BigDecimal? = null
)
