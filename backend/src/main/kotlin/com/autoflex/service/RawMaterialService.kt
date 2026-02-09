package com.autoflex.service

import com.autoflex.dto.CreateRawMaterialRequest
import com.autoflex.dto.PageRequest
import com.autoflex.dto.PageResponse
import com.autoflex.dto.RawMaterialDto
import com.autoflex.dto.UpdateRawMaterialRequest
import com.autoflex.entity.RawMaterial
import com.autoflex.exception.BadRequestException
import com.autoflex.exception.NotFoundException
import com.autoflex.repository.RawMaterialRepository
import com.autoflex.util.CodeGenerator
import io.quarkus.cache.CacheInvalidate
import io.quarkus.cache.CacheInvalidateAll
import io.quarkus.cache.CacheResult
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class RawMaterialService(
    private val rawMaterialRepository: RawMaterialRepository,
    @Inject private val codeGenerator: CodeGenerator
) {
    
    /**
     * Gets all raw materials with caching.
     * Cache TTL: 5 minutes
     */
    @CacheResult(cacheName = "raw-materials")
    fun getAll(): List<RawMaterialDto> {
        return rawMaterialRepository.listAll().map { it.toDto() }
    }
    
    fun getAllPaginated(pageRequest: PageRequest): PageResponse<RawMaterialDto> {
        val sortField = pageRequest.getSortField()
        val direction = if (pageRequest.isAscending()) "ASC" else "DESC"
        
        // Criar query com ordenação e paginação usando find() com HQL
        val page = rawMaterialRepository.find("ORDER BY $sortField $direction")
            .page(pageRequest.page, pageRequest.size)
        
        val totalElements = rawMaterialRepository.count()
        val content = page.list().map { rawMaterial: RawMaterial -> rawMaterial.toDto() }
        
        return PageResponse.of(
            content = content,
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = totalElements
        )
    }
    
    /**
     * Gets a raw material by code with caching.
     * Cache TTL: 10 minutes
     */
    @CacheResult(cacheName = "raw-material-by-code")
    fun getByCode(code: String): RawMaterialDto {
        val rawMaterial = rawMaterialRepository.findByCode(code)
            ?: throw NotFoundException("Raw material with code $code not found")
        return rawMaterial.toDto()
    }
    
    @Transactional
    @CacheInvalidateAll(cacheName = "raw-materials")
    fun create(request: CreateRawMaterialRequest): RawMaterialDto {
        // Use thread-safe code generator with PostgreSQL SEQUENCE
        // This eliminates race conditions that occur with count() + 1 approach
        val code = codeGenerator.generateRawMaterialCode()
        
        // Double-check for uniqueness (defensive programming)
        // This should never happen with SEQUENCE, but provides extra safety
        if (rawMaterialRepository.existsByCode(code)) {
            // Retry with next sequence value if collision occurs (extremely rare)
            val retryCode = codeGenerator.generateRawMaterialCode()
            if (rawMaterialRepository.existsByCode(retryCode)) {
                throw BadRequestException("Unable to generate unique raw material code")
            }
            return createWithCode(request, retryCode)
        }
        
        return createWithCode(request, code)
    }
    
    private fun createWithCode(request: CreateRawMaterialRequest, code: String): RawMaterialDto {
        val rawMaterial = RawMaterial().apply {
            this.code = code
            this.name = request.name
            this.stockQuantity = request.stockQuantity
        }
        
        rawMaterialRepository.persist(rawMaterial)
        return rawMaterial.toDto()
    }
    
    @Transactional
    @CacheInvalidate(cacheName = "raw-material-by-code")
    @CacheInvalidateAll(cacheName = "raw-materials")
    fun update(code: String, request: UpdateRawMaterialRequest): RawMaterialDto {
        val rawMaterial = rawMaterialRepository.findByCode(code)
            ?: throw NotFoundException("Raw material with code $code not found")
        
        request.name?.let { rawMaterial.name = it }
        request.stockQuantity?.let { rawMaterial.stockQuantity = it }
        
        rawMaterialRepository.persist(rawMaterial)
        return rawMaterial.toDto()
    }
    
    @Transactional
    @CacheInvalidate(cacheName = "raw-material-by-code")
    @CacheInvalidateAll(cacheName = "raw-materials")
    fun delete(code: String) {
        val rawMaterial = rawMaterialRepository.findByCode(code)
            ?: throw NotFoundException("Raw material with code $code not found")
        
        rawMaterialRepository.delete(rawMaterial)
    }
    
    
    private fun RawMaterial.toDto(): RawMaterialDto {
        return RawMaterialDto(
            code = this.code,
            name = this.name,
            stockQuantity = this.stockQuantity
        )
    }
}
