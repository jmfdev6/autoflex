package com.autoflex.service

import com.autoflex.dto.CreateRawMaterialRequest
import com.autoflex.dto.UpdateRawMaterialRequest
import com.autoflex.entity.RawMaterial
import com.autoflex.exception.NotFoundException
import com.autoflex.repository.RawMaterialRepository
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@QuarkusTest
class RawMaterialServiceTest {
    
    @Inject
    lateinit var rawMaterialService: RawMaterialService
    
    @Inject
    lateinit var rawMaterialRepository: RawMaterialRepository
    
    @BeforeEach
    @Transactional
    fun setUp() {
        rawMaterialRepository.deleteAll()
    }
    
    @Test
    @Transactional
    fun `should create raw material with generated code`() {
        val request = CreateRawMaterialRequest(
            name = "Test Raw Material",
            stockQuantity = BigDecimal("100.00")
        )
        
        val result = rawMaterialService.create(request)
        
        assertNotNull(result.code)
        assertTrue(result.code.startsWith("RM"))
        assertEquals("Test Raw Material", result.name)
        assertEquals(BigDecimal("100.00"), result.stockQuantity)
    }
    
    @Test
    @Transactional
    fun `should get all raw materials`() {
        val rm1 = RawMaterial().apply {
            code = "RM001"
            name = "Raw Material 1"
            stockQuantity = BigDecimal("100.00")
        }
        val rm2 = RawMaterial().apply {
            code = "RM002"
            name = "Raw Material 2"
            stockQuantity = BigDecimal("200.00")
        }
        rawMaterialRepository.persist(rm1, rm2)
        
        val result = rawMaterialService.getAll()
        
        assertEquals(2, result.size)
    }
    
    @Test
    @Transactional
    fun `should update raw material`() {
        val rawMaterial = RawMaterial().apply {
            code = "RM001"
            name = "Old Name"
            stockQuantity = BigDecimal("100.00")
        }
        rawMaterialRepository.persist(rawMaterial)
        
        val request = UpdateRawMaterialRequest(
            name = "New Name",
            stockQuantity = BigDecimal("200.00")
        )
        
        val result = rawMaterialService.update("RM001", request)
        
        assertEquals("New Name", result.name)
        assertEquals(BigDecimal("200.00"), result.stockQuantity)
    }
    
    @Test
    @Transactional
    fun `should throw NotFoundException when raw material not found`() {
        assertThrows(NotFoundException::class.java) {
            rawMaterialService.getByCode("NONEXISTENT")
        }
    }
}
