package com.autoflex.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

@Entity
@Table(name = "production_items")
open class ProductionItem : PanacheEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_id", nullable = false, updatable = false)
    @NotNull(message = "Production is required")
    lateinit var production: Production

    @Column(name = "product_code", length = 50, nullable = false, updatable = false)
    @NotBlank(message = "Product code is required")
    lateinit var productCode: String

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    var quantity: Int = 0
}
