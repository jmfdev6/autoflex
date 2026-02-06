package com.autoflex.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

@Entity
@Table(
    name = "PRODUCT_RAW_MATERIALS",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["product_code", "raw_material_code"])
    ]
)
open class ProductRawMaterial : PanacheEntity() {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    @NotNull(message = "Product is required")
    lateinit var product: Product
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false, updatable = false)
    @NotNull(message = "Raw material is required")
    lateinit var rawMaterial: RawMaterial
    
    @Column(name = "product_code", length = 50, nullable = false, updatable = false)
    lateinit var productCode: String
    
    @Column(name = "raw_material_code", length = 50, nullable = false, updatable = false)
    lateinit var rawMaterialCode: String
    
    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    var quantity: BigDecimal = BigDecimal.ZERO
    
    @PrePersist
    fun updateCodes() {
        productCode = product.code
        rawMaterialCode = rawMaterial.code
    }
}
