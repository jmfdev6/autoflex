package com.autoflex.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

@Entity
@Table(name = "PRODUCTS")
open class Product : PanacheEntity() {
    
    @Column(name = "code", length = 50, unique = true, nullable = false, updatable = false)
    @NotBlank(message = "Product code is required")
    lateinit var code: String
    
    @Column(name = "name", length = 255, nullable = false)
    @NotBlank(message = "Product name is required")
    lateinit var name: String
    
    @Column(name = "value", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Product value is required")
    @Positive(message = "Product value must be positive")
    var value: BigDecimal = BigDecimal.ZERO
    
    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0
    
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    var rawMaterials: MutableList<ProductRawMaterial> = mutableListOf()
}
