package com.autoflex.repository

import com.autoflex.entity.Product
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductRepository : PanacheRepository<Product> {
    
    fun findByCode(code: String): Product? {
        return find("code", code).firstResult()
    }
    
    fun existsByCode(code: String): Boolean {
        return count("code", code) > 0
    }
}
