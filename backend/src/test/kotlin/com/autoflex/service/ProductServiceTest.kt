package com.autoflex.service

import com.autoflex.dto.CreateProductRequest
import com.autoflex.dto.UpdateProductRequest
import com.autoflex.entity.Product
import com.autoflex.exception.NotFoundException
import com.autoflex.repository.ProductRepository
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@QuarkusTest
class ProductServiceTest {
    
    @Inject
    lateinit var productService: ProductService
    
    @Inject
    lateinit var productRepository: ProductRepository
    
    @BeforeEach
    @Transactional
    fun setUp() {
        productRepository.deleteAll()
    }
    
    @Test
    @Transactional
    fun `should create product with generated code`() {
        val request = CreateProductRequest(
            name = "Test Product",
            value = BigDecimal("100.50")
        )
        
        val result = productService.create(request)
        
        assertNotNull(result.code)
        assertTrue(result.code.startsWith("P"))
        assertEquals("Test Product", result.name)
        assertEquals(BigDecimal("100.50"), result.value)
    }
    
    @Test
    @Transactional
    fun `should get all products`() {
        val product1 = Product().apply {
            code = "P001"
            name = "Product 1"
            value = BigDecimal("100.00")
        }
        val product2 = Product().apply {
            code = "P002"
            name = "Product 2"
            value = BigDecimal("200.00")
        }
        productRepository.persist(product1, product2)
        
        val result = productService.getAll()
        
        assertEquals(2, result.size)
    }
    
    @Test
    @Transactional
    fun `should get product by code`() {
        val product = Product().apply {
            code = "P001"
            name = "Test Product"
            value = BigDecimal("100.00")
        }
        productRepository.persist(product)
        
        val result = productService.getByCode("P001")
        
        assertEquals("P001", result.code)
        assertEquals("Test Product", result.name)
    }
    
    @Test
    @Transactional
    fun `should throw NotFoundException when product not found`() {
        assertThrows(NotFoundException::class.java) {
            productService.getByCode("NONEXISTENT")
        }
    }
    
    @Test
    @Transactional
    fun `should update product`() {
        val product = Product().apply {
            code = "P001"
            name = "Old Name"
            value = BigDecimal("100.00")
        }
        productRepository.persist(product)
        
        val request = UpdateProductRequest(
            name = "New Name",
            value = BigDecimal("200.00")
        )
        
        val result = productService.update("P001", request)
        
        assertEquals("New Name", result.name)
        assertEquals(BigDecimal("200.00"), result.value)
    }
    
    @Test
    @Transactional
    fun `should delete product`() {
        val product = Product().apply {
            code = "P001"
            name = "Test Product"
            value = BigDecimal("100.00")
        }
        productRepository.persist(product)
        
        productService.delete("P001")
        
        assertNull(productRepository.findByCode("P001"))
    }
}
