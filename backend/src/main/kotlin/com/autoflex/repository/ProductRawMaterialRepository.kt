package com.autoflex.repository

import com.autoflex.entity.ProductRawMaterial
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductRawMaterialRepository : PanacheRepository<ProductRawMaterial> {
    
    fun findByProductCode(productCode: String): List<ProductRawMaterial> {
        return find("productCode", productCode).list()
    }
    
    fun findByProductCodeAndRawMaterialCode(
        productCode: String,
        rawMaterialCode: String
    ): ProductRawMaterial? {
        return find("productCode = ?1 and rawMaterialCode = ?2", productCode, rawMaterialCode)
            .firstResult()
    }
    
    fun deleteByProductCode(productCode: String) {
        delete("productCode", productCode)
    }
    
    fun deleteByProductCodeAndRawMaterialCode(
        productCode: String,
        rawMaterialCode: String
    ) {
        delete("productCode = ?1 and rawMaterialCode = ?2", productCode, rawMaterialCode)
    }
}
