package com.autoflex.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

@Entity
@Table(name = "RAW_MATERIALS")
open class RawMaterial : PanacheEntity() {
    
    @Column(name = "code", length = 50, unique = true, nullable = false, updatable = false)
    @NotBlank(message = "Raw material code is required")
    lateinit var code: String
    
    @Column(name = "name", length = 255, nullable = false)
    @NotBlank(message = "Raw material name is required")
    lateinit var name: String
    
    @Column(name = "stock_quantity", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    var stockQuantity: BigDecimal = BigDecimal.ZERO
    
    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
    
    @OneToMany(mappedBy = "rawMaterial", cascade = [CascadeType.ALL], orphanRemoval = true)
    var products: MutableList<ProductRawMaterial> = mutableListOf()
}
