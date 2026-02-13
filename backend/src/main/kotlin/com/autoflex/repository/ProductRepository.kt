package com.autoflex.repository

import com.autoflex.entity.Product
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Query

@ApplicationScoped
class ProductRepository : PanacheRepository<Product> {
    
    fun findByCode(code: String): Product? {
        return find("code", code).firstResult()
    }
    
    fun existsByCode(code: String): Boolean {
        return count("code", code) > 0
    }
    
    /**
     * Full-text search on product names using PostgreSQL tsvector.
     * Returns products whose names match the search term.
     */
    fun searchByName(searchTerm: String): List<Product> {
        return find("""
            SELECT p FROM Product p 
            WHERE to_tsvector('portuguese', p.name) @@ plainto_tsquery('portuguese', :searchTerm)
            ORDER BY ts_rank(to_tsvector('portuguese', p.name), plainto_tsquery('portuguese', :searchTerm)) DESC
        """.trimIndent(), mapOf("searchTerm" to searchTerm)).list()
    }
    
    /**
     * Search products by name using LIKE (case-insensitive).
     * Fallback when full-text search is not needed.
     */
    fun findByNameContaining(name: String): List<Product> {
        return find("LOWER(name) LIKE LOWER(?1)", "%${name}%").list()
    }
}
