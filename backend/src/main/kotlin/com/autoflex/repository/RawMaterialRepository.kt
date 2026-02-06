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
}
