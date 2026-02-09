package com.autoflex.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Business metrics for monitoring application performance and usage.
 * 
 * Tracks:
 * - Product operations (create, update, delete)
 * - Raw material operations
 * - API request counts and durations
 * - Error rates
 */
@ApplicationScoped
class BusinessMetrics @Inject constructor(
    private val registry: MeterRegistry
) {
    
    // Product metrics
    private val productCreatedCounter: Counter = Counter.builder("autoflex.products.created")
        .description("Total number of products created")
        .register(registry)
    
    private val productUpdatedCounter: Counter = Counter.builder("autoflex.products.updated")
        .description("Total number of products updated")
        .register(registry)
    
    private val productDeletedCounter: Counter = Counter.builder("autoflex.products.deleted")
        .description("Total number of products deleted")
        .register(registry)
    
    // Raw material metrics
    private val rawMaterialCreatedCounter: Counter = Counter.builder("autoflex.raw_materials.created")
        .description("Total number of raw materials created")
        .register(registry)
    
    private val rawMaterialUpdatedCounter: Counter = Counter.builder("autoflex.raw_materials.updated")
        .description("Total number of raw materials updated")
        .register(registry)
    
    private val rawMaterialDeletedCounter: Counter = Counter.builder("autoflex.raw_materials.deleted")
        .description("Total number of raw materials deleted")
        .register(registry)
    
    // API metrics
    private val apiErrorCounter: Counter = Counter.builder("autoflex.api.errors")
        .description("Total number of API errors (4xx, 5xx)")
        .tag("type", "api")
        .register(registry)
    
    private val apiRequestTimer: Timer = Timer.builder("autoflex.api.requests.duration")
        .description("API request duration")
        .register(registry)
    
    // Authentication metrics
    private val authSuccessCounter: Counter = Counter.builder("autoflex.auth.success")
        .description("Total number of successful authentications")
        .register(registry)
    
    private val authFailureCounter: Counter = Counter.builder("autoflex.auth.failure")
        .description("Total number of failed authentication attempts")
        .register(registry)
    
    fun recordProductCreated() {
        productCreatedCounter.increment()
    }
    
    fun recordProductUpdated() {
        productUpdatedCounter.increment()
    }
    
    fun recordProductDeleted() {
        productDeletedCounter.increment()
    }
    
    fun recordRawMaterialCreated() {
        rawMaterialCreatedCounter.increment()
    }
    
    fun recordRawMaterialUpdated() {
        rawMaterialUpdatedCounter.increment()
    }
    
    fun recordRawMaterialDeleted() {
        rawMaterialDeletedCounter.increment()
    }
    
    fun recordApiError() {
        apiErrorCounter.increment()
    }
    
    fun recordApiRequest(durationMs: Long) {
        apiRequestTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS)
    }
    
    fun recordAuthSuccess() {
        authSuccessCounter.increment()
    }
    
    fun recordAuthFailure() {
        authFailureCounter.increment()
    }
}
