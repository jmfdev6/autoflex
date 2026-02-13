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
    
    companion object {
        /** Whitelist of allowed sequence names to prevent SQL injection */
        private val ALLOWED_SEQUENCES = setOf("product_code_sequence", "raw_material_code_sequence")
    }

    /**
     * Gets the next value from a PostgreSQL SEQUENCE.
     * This is atomic and thread-safe.
     *
     * Note: PostgreSQL's nextval() requires a string literal; JPA named parameters
     * cannot be used for function arguments. The sequence name is validated against
     * a whitelist to prevent SQL injection.
     */
    private fun getNextSequenceValue(sequenceName: String): Long {
        require(sequenceName in ALLOWED_SEQUENCES) {
            "Invalid sequence name: $sequenceName"
        }
        val query = entityManager.createNativeQuery("SELECT nextval('$sequenceName')")
        val result = query.singleResult
        return when (result) {
            is Number -> result.toLong()
            else -> throw IllegalStateException("Unexpected sequence result type: ${result?.javaClass}")
        }
    }
}
