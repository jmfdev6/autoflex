package com.autoflex.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class ProductionSuggestionDto(
    val product: ProductDto,
    val producibleQuantity: Int,
    val totalValue: BigDecimal
)

data class ProductionSummaryDto(
    val suggestions: List<ProductionSuggestionDto>,
    val totalValue: BigDecimal
)

data class ProductionItemRequest @JsonCreator constructor(
    @JsonProperty("productCode")
    @field:NotBlank(message = "Product code is required")
    @field:jakarta.validation.constraints.Size(min = 1, max = 50, message = "Product code must be between 1 and 50 characters")
    val productCode: String,
    
    @JsonProperty("quantity")
    @field:NotNull(message = "Quantity is required")
    @field:Positive(message = "Quantity must be positive")
    @field:jakarta.validation.constraints.Min(value = 1, message = "Quantity must be at least 1")
    @field:jakarta.validation.constraints.Max(value = 1000000, message = "Quantity must be at most 1,000,000")
    val quantity: Int
)

data class ConfirmProductionRequest @JsonCreator constructor(
    @JsonProperty("items")
    @field:NotNull(message = "Items list is required")
    @field:jakarta.validation.constraints.Size(min = 1, max = 100, message = "Items list must contain between 1 and 100 items")
    val items: List<@jakarta.validation.Valid ProductionItemRequest>
)

data class ConfirmProductionItemResult(
    val productCode: String,
    val productName: String,
    val quantity: Int,
    val totalValue: BigDecimal,
    val success: Boolean,
    val message: String? = null
)

data class ConfirmProductionResponse(
    val items: List<ConfirmProductionItemResult>,
    val totalValue: BigDecimal,
    val successCount: Int,
    val failureCount: Int
)
