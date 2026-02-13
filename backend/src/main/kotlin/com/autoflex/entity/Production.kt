package com.autoflex.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import java.time.Instant

enum class ProductionStatus {
    PENDING,
    CONFIRMED
}

@Entity
@Table(name = "productions")
open class Production : PanacheEntity() {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    var status: ProductionStatus = ProductionStatus.PENDING

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()

    @OneToMany(mappedBy = "production", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<ProductionItem> = mutableListOf()
}
