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
    val productCode: String,
    
    @JsonProperty("quantity")
    @field:NotNull(message = "Quantity is required")
    @field:Positive(message = "Quantity must be positive")
    val quantity: Int
)

data class ConfirmProductionRequest @JsonCreator constructor(
    @JsonProperty("items")
    @field:NotNull(message = "Items list is required")
    val items: List<ProductionItemRequest>
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
