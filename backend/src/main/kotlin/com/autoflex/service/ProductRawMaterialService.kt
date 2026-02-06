package com.autoflex.service

import com.autoflex.dto.CreateProductRawMaterialRequest
import com.autoflex.dto.ProductRawMaterialDto
import com.autoflex.dto.UpdateProductRawMaterialRequest
import com.autoflex.entity.ProductRawMaterial
import com.autoflex.exception.BadRequestException
import com.autoflex.exception.NotFoundException
import com.autoflex.repository.ProductRawMaterialRepository
import com.autoflex.repository.ProductRepository
import com.autoflex.repository.RawMaterialRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class ProductRawMaterialService(
    private val productRawMaterialRepository: ProductRawMaterialRepository,
    private val productRepository: ProductRepository,
    private val rawMaterialRepository: RawMaterialRepository
) {
    
    fun getByProductCode(productCode: String): List<ProductRawMaterialDto> {
        productRepository.findByCode(productCode)
            ?: throw NotFoundException("Product with code $productCode not found")
        
        return productRawMaterialRepository.findByProductCode(productCode)
            .map { it.toDto() }
    }
    
    @Transactional
    fun create(productCode: String, request: CreateProductRawMaterialRequest): ProductRawMaterialDto {
        val product = productRepository.findByCode(productCode)
            ?: throw NotFoundException("Product with code $productCode not found")
        
        val rawMaterial = rawMaterialRepository.findByCode(request.rawMaterialCode)
            ?: throw NotFoundException("Raw material with code ${request.rawMaterialCode} not found")
        
        val existing = productRawMaterialRepository.findByProductCodeAndRawMaterialCode(
            productCode,
            request.rawMaterialCode
        )
        
        if (existing != null) {
            throw BadRequestException("Association already exists")
        }
        
        val productRawMaterial = ProductRawMaterial().apply {
            this.product = product
            this.rawMaterial = rawMaterial
            this.quantity = request.quantity
        }
        
        productRawMaterialRepository.persist(productRawMaterial)
        return productRawMaterial.toDto()
    }
    
    @Transactional
    fun update(
        productCode: String,
        rawMaterialCode: String,
        request: UpdateProductRawMaterialRequest
    ): ProductRawMaterialDto {
        val productRawMaterial = productRawMaterialRepository.findByProductCodeAndRawMaterialCode(
            productCode,
            rawMaterialCode
        ) ?: throw NotFoundException("Association not found")
        
        productRawMaterial.quantity = request.quantity
        productRawMaterialRepository.persist(productRawMaterial)
        
        return productRawMaterial.toDto()
    }
    
    @Transactional
    fun delete(productCode: String, rawMaterialCode: String) {
        val productRawMaterial = productRawMaterialRepository.findByProductCodeAndRawMaterialCode(
            productCode,
            rawMaterialCode
        ) ?: throw NotFoundException("Association not found")
        
        productRawMaterialRepository.delete(productRawMaterial)
    }
    
    private fun ProductRawMaterial.toDto(): ProductRawMaterialDto {
        return ProductRawMaterialDto(
            productCode = this.productCode,
            rawMaterialCode = this.rawMaterialCode,
            quantity = this.quantity,
            rawMaterialName = this.rawMaterial.name,
            productName = this.product.name
        )
    }
}
