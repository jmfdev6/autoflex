package com.autoflex.util

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional

/**
 * Thread-safe code generator using PostgreSQL SEQUENCE.
 * This eliminates race conditions that occur when using count() + 1 approach.
 */
@ApplicationScoped
class CodeGenerator(
    @Inject private val entityManager: EntityManager
) {
    
    /**
     * Generates a unique product code using PostgreSQL SEQUENCE.
     * Format: P001, P002, P003, etc.
     * 
     * This method is thread-safe and handles concurrent requests correctly.
     */
    @Transactional
    fun generateProductCode(): String {
        val nextValue = getNextSequenceValue("product_code_sequence")
        return "P${String.format("%03d", nextValue)}"
    }
    
    /**
     * Generates a unique raw material code using PostgreSQL SEQUENCE.
     * Format: RM001, RM002, RM003, etc.
     * 
     * This method is thread-safe and handles concurrent requests correctly.
     */
    @Transactional
    fun generateRawMaterialCode(): String {
        val nextValue = getNextSequenceValue("raw_material_code_sequence")
        return "RM${String.format("%03d", nextValue)}"
    }
    
    /**
     * Gets the next value from a PostgreSQL SEQUENCE.
     * This is atomic and thread-safe.
     */
    private fun getNextSequenceValue(sequenceName: String): Long {
        val query = entityManager.createNativeQuery("SELECT nextval(:sequenceName)")
        query.setParameter("sequenceName", sequenceName)
        val result = query.singleResult
        return when (result) {
            is Number -> result.toLong()
            else -> throw IllegalStateException("Unexpected sequence result type: ${result?.javaClass}")
        }
    }
}
