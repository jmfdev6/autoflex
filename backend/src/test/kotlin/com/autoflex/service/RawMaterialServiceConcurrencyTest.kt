package com.autoflex.service

import com.autoflex.dto.CreateRawMaterialRequest
import com.autoflex.repository.RawMaterialRepository
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Concurrency tests for RawMaterialService to ensure thread-safe code generation.
 * 
 * These tests verify that the SEQUENCE-based code generation eliminates race conditions
 * that would occur with the old count() + 1 approach.
 */
@QuarkusTest
class RawMaterialServiceConcurrencyTest {
    
    @Inject
    lateinit var rawMaterialService: RawMaterialService
    
    @Inject
    lateinit var rawMaterialRepository: RawMaterialRepository
    
    @BeforeEach
    fun setUp() {
        // Clean up test data
        rawMaterialRepository.deleteAll()
    }
    
    @Test
    fun `should generate unique codes under concurrent load`() {
        val numberOfThreads = 50
        val materialsPerThread = 10
        val totalMaterials = numberOfThreads * materialsPerThread
        
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val futures = mutableListOf<Future<*>>()
        val generatedCodes = ConcurrentHashMap<String, Boolean>()
        val errors = ConcurrentLinkedQueue<Throwable>()
        
        // Submit concurrent raw material creation tasks
        repeat(numberOfThreads) { threadIndex ->
            val future = executor.submit {
                repeat(materialsPerThread) {
                    try {
                        val request = CreateRawMaterialRequest(
                            name = "Raw Material Thread-$threadIndex Item-$it",
                            stockQuantity = BigDecimal("100.0")
                        )
                        val rawMaterial = rawMaterialService.create(request)
                        
                        // Verify code is unique
                        val wasNew = generatedCodes.putIfAbsent(rawMaterial.code, true) == null
                        assertTrue(wasNew, "Duplicate code generated: ${rawMaterial.code}")
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
        assertEquals(totalMaterials, generatedCodes.size, "Expected $totalMaterials unique codes, got ${generatedCodes.size}")
        
        // Verify all raw materials were persisted
        val persistedCount = rawMaterialRepository.count()
        assertEquals(totalMaterials, persistedCount, "Expected $totalMaterials raw materials in database, got $persistedCount")
        
        // Verify all codes follow the pattern RM001, RM002, etc.
        generatedCodes.keys.forEach { code ->
            assertTrue(code.matches(Regex("^RM\\d{3}$")), "Code $code does not match pattern RM###")
        }
    }
}
