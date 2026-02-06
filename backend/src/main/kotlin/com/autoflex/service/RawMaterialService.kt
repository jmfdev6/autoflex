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
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class RawMaterialService(
    private val rawMaterialRepository: RawMaterialRepository
) {
    
    fun getAll(): List<RawMaterialDto> {
        return rawMaterialRepository.listAll().map { it.toDto() }
    }
    
    fun getAllPaginated(pageRequest: PageRequest): PageResponse<RawMaterialDto> {
        val sortField = pageRequest.getSortField()
        val query = rawMaterialRepository.findAll()
        
        // Aplicar ordenação
        val sortedQuery = if (pageRequest.isAscending()) {
            query.orderBy(sortField)
        } else {
            query.orderBy("$sortField desc")
        }
        
        // Paginar
        val page = sortedQuery.page(pageRequest.page, pageRequest.size)
        val totalElements = rawMaterialRepository.count()
        
        val content = page.list().map { it.toDto() }
        
        return PageResponse.of(
            content = content,
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = totalElements
        )
    }
    
    fun getByCode(code: String): RawMaterialDto {
        val rawMaterial = rawMaterialRepository.findByCode(code)
            ?: throw NotFoundException("Raw material with code $code not found")
        return rawMaterial.toDto()
    }
    
    @Transactional
    fun create(request: CreateRawMaterialRequest): RawMaterialDto {
        val code = generateRawMaterialCode()
        
        if (rawMaterialRepository.existsByCode(code)) {
            throw BadRequestException("Raw material code $code already exists")
        }
        
        val rawMaterial = RawMaterial().apply {
            this.code = code
            this.name = request.name
            this.stockQuantity = request.stockQuantity
        }
        
        rawMaterialRepository.persist(rawMaterial)
        return rawMaterial.toDto()
    }
    
    @Transactional
    fun update(code: String, request: UpdateRawMaterialRequest): RawMaterialDto {
        val rawMaterial = rawMaterialRepository.findByCode(code)
            ?: throw NotFoundException("Raw material with code $code not found")
        
        request.name?.let { rawMaterial.name = it }
        request.stockQuantity?.let { rawMaterial.stockQuantity = it }
        
        rawMaterialRepository.persist(rawMaterial)
        return rawMaterial.toDto()
    }
    
    @Transactional
    fun delete(code: String) {
        val rawMaterial = rawMaterialRepository.findByCode(code)
            ?: throw NotFoundException("Raw material with code $code not found")
        
        rawMaterialRepository.delete(rawMaterial)
    }
    
    private fun generateRawMaterialCode(): String {
        val count = rawMaterialRepository.count()
        return "RM${String.format("%03d", count + 1)}"
    }
    
    private fun RawMaterial.toDto(): RawMaterialDto {
        return RawMaterialDto(
            code = this.code,
            name = this.name,
            stockQuantity = this.stockQuantity
        )
    }
}
