package com.autoflex.service

import com.autoflex.dto.CreateProductRequest
import com.autoflex.repository.ProductRepository
import com.autoflex.util.CodeGenerator
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Concurrency tests for ProductService to ensure thread-safe code generation.
 * 
 * These tests verify that the SEQUENCE-based code generation eliminates race conditions
 * that would occur with the old count() + 1 approach.
 */
@QuarkusTest
class ProductServiceConcurrencyTest {
    
    @Inject
    lateinit var productService: ProductService
    
    @Inject
    lateinit var productRepository: ProductRepository
    
    @BeforeEach
    fun setUp() {
        // Clean up test data
        productRepository.deleteAll()
    }
    
    @Test
    fun `should generate unique codes under concurrent load`() {
        val numberOfThreads = 50
        val productsPerThread = 10
        val totalProducts = numberOfThreads * productsPerThread
        
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val futures = mutableListOf<Future<*>>()
        val generatedCodes = ConcurrentHashMap<String, Boolean>()
        val errors = ConcurrentLinkedQueue<Throwable>()
        
        // Submit concurrent product creation tasks
        repeat(numberOfThreads) { threadIndex ->
            val future = executor.submit {
                repeat(productsPerThread) {
                    try {
                        val request = CreateProductRequest(
                            name = "Product Thread-$threadIndex Item-$it",
                            value = BigDecimal("100.00")
                        )
                        val product = productService.create(request)
                        
                        // Verify code is unique
                        val wasNew = generatedCodes.putIfAbsent(product.code, true) == null
                        assertTrue(wasNew, "Duplicate code generated: ${product.code}")
                    } catch (e: Exception) {
                        errors.add(e)
                    }
                }
            }
            futures.add(future)
        }
        
        // Wait for all tasks to complete
        futures.forEach { it.get(30, TimeUnit.SECONDS) }
        executor.shutdown()
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS))
        
        // Verify no errors occurred
        assertTrue(errors.isEmpty(), "Errors occurred during concurrent execution: ${errors.joinToString("\n")}")
        
        // Verify all codes were generated
        assertEquals(totalProducts, generatedCodes.size, "Expected $totalProducts unique codes, got ${generatedCodes.size}")
        
        // Verify all products were persisted
        val persistedCount = productRepository.count()
        assertEquals(totalProducts, persistedCount, "Expected $totalProducts products in database, got $persistedCount")
        
        // Verify all codes follow the pattern P001, P002, etc.
        generatedCodes.keys.forEach { code ->
            assertTrue(code.matches(Regex("^P\\d{3}$")), "Code $code does not match pattern P###")
        }
    }
    
    @Test
    fun `should handle high concurrency without deadlocks`() {
        val numberOfThreads = 100
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val futures = mutableListOf<Future<*>>()
        val successCount = AtomicInteger(0)
        val errorCount = AtomicInteger(0)
        
        // Submit many concurrent requests
        repeat(numberOfThreads) {
            val future = executor.submit {
                try {
                    val request = CreateProductRequest(
                        name = "Concurrent Product $it",
                        value = BigDecimal("50.00")
                    )
                    productService.create(request)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    errorCount.incrementAndGet()
                    e.printStackTrace()
                }
            }
            futures.add(future)
        }
        
        // Wait for all tasks with timeout
        futures.forEach { 
            try {
                it.get(60, TimeUnit.SECONDS)
            } catch (e: TimeoutException) {
                fail("Test timed out - possible deadlock")
            }
        }
        
        executor.shutdown()
        
        // Verify most requests succeeded
        assertTrue(successCount.get() > numberOfThreads * 0.95, 
            "Expected >95% success rate, got ${successCount.get()}/$numberOfThreads")
        assertTrue(errorCount.get() < numberOfThreads * 0.05,
            "Expected <5% error rate, got ${errorCount.get()}/$numberOfThreads")
    }
    
    @Test
    fun `should generate sequential codes without gaps under normal conditions`() {
        val numberOfProducts = 20
        
        // Create products sequentially
        val codes = mutableListOf<String>()
        repeat(numberOfProducts) {
            val request = CreateProductRequest(
                name = "Sequential Product $it",
                value = BigDecimal("75.00")
            )
            val product = productService.create(request)
            codes.add(product.code)
        }
        
        // Verify codes are sequential (P001, P002, P003, etc.)
        codes.sorted().forEachIndexed { index, code ->
            val expectedNumber = index + 1
            val expectedCode = "P${String.format("%03d", expectedNumber)}"
            assertEquals(expectedCode, code, "Code at index $index should be $expectedCode")
        }
    }
}
