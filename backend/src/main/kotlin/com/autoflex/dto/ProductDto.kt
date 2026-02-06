package com.autoflex.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class ProductDto(
    val code: String,
    val name: String,
    val value: BigDecimal
)

data class CreateProductRequest @JsonCreator constructor(
    @JsonProperty("name")
    @field:NotBlank(message = "Product name is required")
    val name: String,
    
    @JsonProperty("value")
    @field:NotNull(message = "Product value is required")
    @field:Positive(message = "Product value must be positive")
    val value: BigDecimal
)

data class UpdateProductRequest @JsonCreator constructor(
    @JsonProperty("name")
    val name: String? = null,
    
    @JsonProperty("value")
    @field:Positive(message = "Product value must be positive")
    val value: BigDecimal? = null
)
