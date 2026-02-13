package com.autoflex.service

import com.autoflex.dto.*
import com.autoflex.entity.Product
import com.autoflex.entity.Production
import com.autoflex.entity.ProductionItem
import com.autoflex.entity.ProductionStatus
import com.autoflex.exception.BadRequestException
import com.autoflex.exception.ConcurrencyException
import com.autoflex.exception.InsufficientStockException
import com.autoflex.exception.NotFoundException
import com.autoflex.repository.ProductRepository
import com.autoflex.repository.RawMaterialRepository
import com.autoflex.repository.ProductRawMaterialRepository
import com.autoflex.repository.ProductionRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.hibernate.StaleObjectStateException
import java.math.BigDecimal
import java.math.RoundingMode

@ApplicationScoped
class ProductionService(
    private val productRepository: ProductRepository,
    private val rawMaterialRepository: RawMaterialRepository,
    private val productRawMaterialRepository: ProductRawMaterialRepository,
    private val productionRepository: ProductionRepository
) {
    
    @Transactional
    fun createProduction(request: ConfirmProductionRequest): ProductionResponseDto {
        val production = Production().apply {
            status = ProductionStatus.PENDING
            createdAt = java.time.Instant.now()
        }
        for (item in request.items) {
            val pi = ProductionItem().apply {
                this.production = production
                productCode = item.productCode
                quantity = item.quantity
            }
            production.items.add(pi)
        }
        productionRepository.persist(production)
        return ProductionResponseDto(
            id = production.id!!,
            status = production.status.name,
            items = production.items.map { ProductionItemDto(it.productCode, it.quantity) },
            createdAt = production.createdAt.toString()
        )
    }
    
    @Transactional
    fun confirmProductionById(id: Long): ConfirmProductionResponse {
        val production = productionRepository.findById(id)
            ?: throw NotFoundException("Production with id $id not found", "PRODUCTION_NOT_FOUND")
        if (production.status != ProductionStatus.PENDING) {
            throw BadRequestException("Production $id is already confirmed or invalid")
        }
        val request = ConfirmProductionRequest(
            items = production.items.map { ProductionItemRequest(it.productCode, it.quantity) }
        )
        val response = confirmProduction(request)
        production.status = ProductionStatus.CONFIRMED
        productionRepository.persist(production)
        return response
    }
    
    @Transactional
    fun getProductionSuggestions(): ProductionSummaryDto {
        // Buscar todos os produtos ordenados por valor (descendente)
        val products = productRepository.listAll()
            .sortedByDescending { it.value }
        
        // Buscar todas as matérias-primas com lock para garantir consistência
        val rawMaterials = rawMaterialRepository.findAllWithLock()
        val stockMap = rawMaterials.associate { it.code to it.stockQuantity }
        
        // Buscar todas as associações
        val allAssociations = productRawMaterialRepository.listAll()
        
       
        val suggestions = mutableListOf<ProductionSuggestionDto>()
        val usedStock = mutableMapOf<String, BigDecimal>()
        
        for (product in products) {
            val productAssociations = allAssociations.filter { 
                it.productCode == product.code 
            }
            
            if (productAssociations.isEmpty()) {
                continue
            }
            
            // Calcular quantidade máxima produzível
            var maxProducible = Int.MAX_VALUE
            
            for (association in productAssociations) {
                val rawMaterialCode = association.rawMaterialCode
                val available = stockMap[rawMaterialCode] ?: BigDecimal.ZERO
                val used = usedStock[rawMaterialCode] ?: BigDecimal.ZERO
                val remaining = available.subtract(used)
                
                if (remaining <= BigDecimal.ZERO || association.quantity <= BigDecimal.ZERO) {
                    maxProducible = 0
                    break
                }
                
                val producibleForThisMaterial = remaining.divide(
                    association.quantity,
                    0,
                    RoundingMode.FLOOR
                ).toInt()
                
                maxProducible = minOf(maxProducible, producibleForThisMaterial)
            }
            
            if (maxProducible > 0) {
                // Atualizar estoque usado
                for (association in productAssociations) {
                    val rawMaterialCode = association.rawMaterialCode
                    val currentUsed = usedStock[rawMaterialCode] ?: BigDecimal.ZERO
                    val additionalUsed = association.quantity.multiply(BigDecimal(maxProducible))
                    usedStock[rawMaterialCode] = currentUsed.add(additionalUsed)
                }
                
                val totalValue = product.value.multiply(BigDecimal(maxProducible))
                
                suggestions.add(
                    ProductionSuggestionDto(
                        product = product.toDto(),
                        producibleQuantity = maxProducible,
                        totalValue = totalValue
                    )
                )
            }
        }
        
        val totalValue = suggestions.fold(BigDecimal.ZERO) { acc, suggestion ->
            acc.add(suggestion.totalValue)
        }
        
        return ProductionSummaryDto(
            suggestions = suggestions,
            totalValue = totalValue
        )
    }
    
    @Transactional
    fun confirmProduction(request: ConfirmProductionRequest): ConfirmProductionResponse {
        val results = mutableListOf<ConfirmProductionItemResult>()
        var totalValue = BigDecimal.ZERO
        var successCount = 0
        var failureCount = 0
        
        for (item in request.items) {
            try {
                val result = confirmProductionItem(item)
                results.add(result)
                totalValue = totalValue.add(result.totalValue)
                successCount++
            } catch (e: Exception) {
                val product = productRepository.findByCode(item.productCode)
                val productName = product?.name ?: item.productCode
                
                val errorMessage = when (e) {
                    is InsufficientStockException -> e.message ?: "Insufficient stock"
                    is ConcurrencyException -> e.message ?: "Concurrency conflict"
                    is NotFoundException -> e.message ?: "Resource not found"
                    else -> "Failed to confirm production: ${e.message}"
                }
                
                results.add(
                    ConfirmProductionItemResult(
                        productCode = item.productCode,
                        productName = productName,
                        quantity = item.quantity,
                        totalValue = BigDecimal.ZERO,
                        success = false,
                        message = errorMessage
                    )
                )
                failureCount++
            }
        }
        
        return ConfirmProductionResponse(
            items = results,
            totalValue = totalValue,
            successCount = successCount,
            failureCount = failureCount
        )
    }
    
    private fun confirmProductionItem(item: ProductionItemRequest): ConfirmProductionItemResult {
        // Buscar produto
        val product = productRepository.findByCode(item.productCode)
            ?: throw NotFoundException("Product with code ${item.productCode} not found", "PRODUCT_NOT_FOUND")
        
        // Buscar associações do produto
        val associations = productRawMaterialRepository.findByProductCode(item.productCode)
        
        if (associations.isEmpty()) {
            throw NotFoundException("No raw materials associated with product ${item.productCode}", "PRODUCT_RAW_MATERIAL_NOT_FOUND")
        }
        
        // Validar estoque disponível com locks pessimistas
        val stockUpdates = mutableMapOf<String, BigDecimal>()
        
        for (association in associations) {
            val rawMaterial = rawMaterialRepository.findByCodeWithLock(association.rawMaterialCode)
                ?: throw NotFoundException("Raw material ${association.rawMaterialCode} not found", "RAW_MATERIAL_NOT_FOUND")
            
            val requiredQuantity = association.quantity.multiply(BigDecimal(item.quantity))
            val availableQuantity = rawMaterial.stockQuantity
            
            if (requiredQuantity > availableQuantity) {
                throw InsufficientStockException(
                    "Insufficient stock for raw material ${association.rawMaterialCode}. " +
                            "Required: $requiredQuantity, Available: $availableQuantity",
                    rawMaterialCode = association.rawMaterialCode,
                    available = availableQuantity,
                    requested = requiredQuantity
                )
            }
            
            // Calcular novo estoque
            val newStock = availableQuantity.subtract(requiredQuantity)
            stockUpdates[rawMaterial.code] = newStock
        }
        
        // Atualizar estoques (com verificação de versão automática pelo Hibernate)
        try {
            for ((rawMaterialCode, newStock) in stockUpdates) {
                val rawMaterial = rawMaterialRepository.findByCodeWithLock(rawMaterialCode)
                    ?: throw NotFoundException("Raw material $rawMaterialCode not found", "RAW_MATERIAL_NOT_FOUND")
                
                rawMaterial.stockQuantity = newStock
                rawMaterialRepository.persist(rawMaterial)
            }
        } catch (e: StaleObjectStateException) {
            throw ConcurrencyException(
                "Concurrency conflict detected. The stock was modified by another transaction. Please try again."
            )
        }
        
        val itemTotalValue = product.value.multiply(BigDecimal(item.quantity))
        
        return ConfirmProductionItemResult(
            productCode = product.code,
            productName = product.name,
            quantity = item.quantity,
            totalValue = itemTotalValue,
            success = true,
            message = "Production confirmed successfully"
        )
    }
    
    private fun Product.toDto(): ProductDto {
        return ProductDto(
            code = this.code,
            name = this.name,
            value = this.value
        )
    }
}
