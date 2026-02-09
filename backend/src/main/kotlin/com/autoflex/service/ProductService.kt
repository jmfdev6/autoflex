package com.autoflex.service

import com.autoflex.dto.CreateProductRequest
import com.autoflex.dto.PageRequest
import com.autoflex.dto.PageResponse
import com.autoflex.dto.ProductDto
import com.autoflex.dto.UpdateProductRequest
import com.autoflex.entity.Product
import com.autoflex.exception.BadRequestException
import com.autoflex.exception.NotFoundException
import com.autoflex.metrics.BusinessMetrics
import com.autoflex.repository.ProductRepository
import com.autoflex.util.CodeGenerator
import io.quarkus.cache.CacheInvalidate
import io.quarkus.cache.CacheInvalidateAll
import io.quarkus.cache.CacheResult
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import java.math.BigDecimal
import java.util.*

@ApplicationScoped
class ProductService(
    private val productRepository: ProductRepository,
    @Inject private val codeGenerator: CodeGenerator,
    @Inject private val businessMetrics: BusinessMetrics
) {
    
    /**
     * Gets all products with caching.
     * Cache TTL: 5 minutes
     */
    @CacheResult(cacheName = "products")
    fun getAll(): List<ProductDto> {
        return productRepository.listAll().map { it.toDto() }
    }
    
    fun getAllPaginated(pageRequest: PageRequest): PageResponse<ProductDto> {
        val sortField = pageRequest.getSortField()
        val direction = if (pageRequest.isAscending()) "ASC" else "DESC"
        
        // Optimized query with proper pagination
        // Using indexed fields for sorting improves performance
        val page = productRepository.find("ORDER BY $sortField $direction")
            .page(pageRequest.page, pageRequest.size)
        
        // Get total count efficiently (uses index if available)
        val totalElements = productRepository.count()
        val content = page.list().map { product: Product -> product.toDto() }
        
        return PageResponse.of(
            content = content,
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = totalElements
        )
    }
    
    /**
     * Search products by name using full-text search.
     * Uses PostgreSQL tsvector for fast text search.
     */
    fun searchByName(searchTerm: String): List<ProductDto> {
        return productRepository.searchByName(searchTerm).map { it.toDto() }
    }
    
    /**
     * Gets a product by code with caching.
     * Cache TTL: 10 minutes
     */
    @CacheResult(cacheName = "product-by-code")
    fun getByCode(code: String): ProductDto {
        val product = productRepository.findByCode(code)
            ?: throw NotFoundException("Product with code $code not found")
        return product.toDto()
    }
    
    @Transactional
    @CacheInvalidateAll(cacheName = "products")
    fun create(request: CreateProductRequest): ProductDto {
        // Use thread-safe code generator with PostgreSQL SEQUENCE
        // This eliminates race conditions that occur with count() + 1 approach
        val code = codeGenerator.generateProductCode()
        
        // Double-check for uniqueness (defensive programming)
        // This should never happen with SEQUENCE, but provides extra safety
        if (productRepository.existsByCode(code)) {
            // Retry with next sequence value if collision occurs (extremely rare)
            val retryCode = codeGenerator.generateProductCode()
            if (productRepository.existsByCode(retryCode)) {
                throw BadRequestException("Unable to generate unique product code")
            }
            return createWithCode(request, retryCode)
        }
        
        return createWithCode(request, code)
    }
    
    private fun createWithCode(request: CreateProductRequest, code: String): ProductDto {
        val product = Product().apply {
            this.code = code
            this.name = request.name
            this.value = request.value
        }
        
        productRepository.persist(product)
        return product.toDto()
    }
    
    @Transactional
    @CacheInvalidate(cacheName = "product-by-code")
    @CacheInvalidateAll(cacheName = "products")
    fun update(code: String, request: UpdateProductRequest): ProductDto {
        val product = productRepository.findByCode(code)
            ?: throw NotFoundException("Product with code $code not found")
        
        request.name?.let { product.name = it }
        request.value?.let { product.value = it }
        
        productRepository.persist(product)
        businessMetrics.recordProductUpdated()
        return product.toDto()
    }
    
    @Transactional
    @CacheInvalidate(cacheName = "product-by-code")
    @CacheInvalidateAll(cacheName = "products")
    fun delete(code: String) {
        val product = productRepository.findByCode(code)
            ?: throw NotFoundException("Product with code $code not found")
        
        productRepository.delete(product)
        businessMetrics.recordProductDeleted()
    }
    
    
    private fun Product.toDto(): ProductDto {
        return ProductDto(
            code = this.code,
            name = this.name,
            value = this.value
        )
    }
}
