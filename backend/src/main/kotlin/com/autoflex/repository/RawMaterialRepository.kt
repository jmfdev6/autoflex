package com.autoflex.repository

import com.autoflex.entity.RawMaterial
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.LockModeType

@ApplicationScoped
class RawMaterialRepository : PanacheRepository<RawMaterial> {
    
    fun findByCode(code: String): RawMaterial? {
        return find("code", code).firstResult()
    }
    
    fun findByCodeWithLock(code: String): RawMaterial? {
        return find("code", code)
            .withLock(LockModeType.PESSIMISTIC_WRITE)
            .firstResult()
    }
    
    fun findAllWithLock(): List<RawMaterial> {
        return findAll()
            .withLock(LockModeType.PESSIMISTIC_WRITE)
            .list()
    }
    
    fun existsByCode(code: String): Boolean {
        return count("code", code) > 0
    }
    
    /**
     * Full-text search on raw material names using PostgreSQL tsvector.
     * Returns raw materials whose names match the search term.
     */
    fun searchByName(searchTerm: String): List<RawMaterial> {
        return find("""
            SELECT r FROM RawMaterial r 
            WHERE to_tsvector('portuguese', r.name) @@ plainto_tsquery('portuguese', :searchTerm)
            ORDER BY ts_rank(to_tsvector('portuguese', r.name), plainto_tsquery('portuguese', :searchTerm)) DESC
        """.trimIndent(), mapOf("searchTerm" to searchTerm)).list()
    }
    
    /**
     * Search raw materials by name using LIKE (case-insensitive).
     * Fallback when full-text search is not needed.
     */
    fun findByNameContaining(name: String): List<RawMaterial> {
        return find("name LIKE ?1", "%${name}%").list()
    }
}
