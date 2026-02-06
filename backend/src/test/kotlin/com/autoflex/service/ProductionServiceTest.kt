package com.autoflex.service

import com.autoflex.entity.Product
import com.autoflex.entity.ProductRawMaterial
import com.autoflex.entity.RawMaterial
import com.autoflex.repository.ProductRawMaterialRepository
import com.autoflex.repository.ProductRepository
import com.autoflex.repository.RawMaterialRepository
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@QuarkusTest
class ProductionServiceTest {
    
    @Inject
    lateinit var productionService: ProductionService
    
    @Inject
    lateinit var productRepository: ProductRepository
    
    @Inject
    lateinit var rawMaterialRepository: RawMaterialRepository
    
    @Inject
    lateinit var productRawMaterialRepository: ProductRawMaterialRepository
    
    @BeforeEach
    @Transactional
    fun setUp() {
        productRawMaterialRepository.deleteAll()
        productRepository.deleteAll()
        rawMaterialRepository.deleteAll()
    }
    
    @Test
    @Transactional
    fun `should prioritize products by value`() {
        // Create raw materials
        val rm1 = RawMaterial().apply {
            code = "RM001"
            name = "Raw Material 1"
            stockQuantity = BigDecimal("100.00")
        }
        val rm2 = RawMaterial().apply {
            code = "RM002"
            name = "Raw Material 2"
            stockQuantity = BigDecimal("50.00")
        }
        rawMaterialRepository.persist(rm1, rm2)
        
        // Create products (lower value first to test prioritization)
        val product1 = Product().apply {
            code = "P001"
            name = "Product 1"
            value = BigDecimal("100.00") // Lower value
        }
        val product2 = Product().apply {
            code = "P002"
            name = "Product 2"
            value = BigDecimal("200.00") // Higher value - should be prioritized
        }
        productRepository.persist(product1, product2)
        
        // Create associations
        val assoc1 = ProductRawMaterial().apply {
            this.product = product1
            this.rawMaterial = rm1
            quantity = BigDecimal("10.00")
        }
        val assoc2 = ProductRawMaterial().apply {
            this.product = product2
            this.rawMaterial = rm1
            quantity = BigDecimal("5.00")
        }
        productRawMaterialRepository.persist(assoc1, assoc2)
        
        val result = productionService.getProductionSuggestions()
        
        assertTrue(result.suggestions.isNotEmpty())
        // Product 2 (higher value) should be first
        assertEquals("P002", result.suggestions.first().product.code)
    }
    
    @Test
    @Transactional
    fun `should calculate producible quantity correctly`() {
        // Create raw material with stock
        val rm1 = RawMaterial().apply {
            code = "RM001"
            name = "Raw Material 1"
            stockQuantity = BigDecimal("100.00")
        }
        rawMaterialRepository.persist(rm1)
        
        // Create product
        val product1 = Product().apply {
            code = "P001"
            name = "Product 1"
            value = BigDecimal("50.00")
        }
        productRepository.persist(product1)
        
        // Create association: 10 units of RM1 needed per product
        val assoc1 = ProductRawMaterial().apply {
            this.product = product1
            this.rawMaterial = rm1
            quantity = BigDecimal("10.00")
        }
        productRawMaterialRepository.persist(assoc1)
        
        val result = productionService.getProductionSuggestions()
        
        assertEquals(1, result.suggestions.size)
        assertEquals(10, result.suggestions.first().producibleQuantity) // 100 / 10 = 10
        assertEquals(BigDecimal("500.00"), result.suggestions.first().totalValue) // 10 * 50
    }
    
    @Test
    @Transactional
    fun `should handle multiple raw materials per product`() {
        // Create raw materials
        val rm1 = RawMaterial().apply {
            code = "RM001"
            name = "Raw Material 1"
            stockQuantity = BigDecimal("100.00")
        }
        val rm2 = RawMaterial().apply {
            code = "RM002"
            name = "Raw Material 2"
            stockQuantity = BigDecimal("30.00") // Limiting factor
        }
        rawMaterialRepository.persist(rm1, rm2)
        
        // Create product
        val product1 = Product().apply {
            code = "P001"
            name = "Product 1"
            value = BigDecimal("50.00")
        }
        productRepository.persist(product1)
        
        // Create associations
        val assoc1 = ProductRawMaterial().apply {
            this.product = product1
            this.rawMaterial = rm1
            quantity = BigDecimal("5.00")
        }
        val assoc2 = ProductRawMaterial().apply {
            this.product = product1
            this.rawMaterial = rm2
            quantity = BigDecimal("10.00")
        }
        productRawMaterialRepository.persist(assoc1, assoc2)
        
        val result = productionService.getProductionSuggestions()
        
        assertEquals(1, result.suggestions.size)
        // Should be limited by RM2: 30 / 10 = 3
        assertEquals(3, result.suggestions.first().producibleQuantity)
    }
    
    @Test
    @Transactional
    fun `should not suggest products without raw materials`() {
        val product1 = Product().apply {
            code = "P001"
            name = "Product 1"
            value = BigDecimal("50.00")
        }
        productRepository.persist(product1)
        
        val result = productionService.getProductionSuggestions()
        
        assertTrue(result.suggestions.isEmpty())
    }
    
    @Test
    @Transactional
    fun `should calculate total value correctly`() {
        val rm1 = RawMaterial().apply {
            code = "RM001"
            name = "Raw Material 1"
            stockQuantity = BigDecimal("100.00")
        }
        rawMaterialRepository.persist(rm1)
        
        val product1 = Product().apply {
            code = "P001"
            name = "Product 1"
            value = BigDecimal("50.00")
        }
        val product2 = Product().apply {
            code = "P002"
            name = "Product 2"
            value = BigDecimal("100.00")
        }
        productRepository.persist(product1, product2)
        
        val assoc1 = ProductRawMaterial().apply {
            this.product = product1
            this.rawMaterial = rm1
            quantity = BigDecimal("20.00")
        }
        val assoc2 = ProductRawMaterial().apply {
            this.product = product2
            this.rawMaterial = rm1
            quantity = BigDecimal("25.00")
        }
        productRawMaterialRepository.persist(assoc1, assoc2)
        
        val result = productionService.getProductionSuggestions()
        
        val totalValue = result.suggestions.fold(BigDecimal.ZERO) { acc, suggestion ->
            acc.add(suggestion.totalValue)
        }
        assertEquals(result.totalValue, totalValue)
    }
}
