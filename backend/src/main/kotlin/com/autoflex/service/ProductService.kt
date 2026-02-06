package com.autoflex.service

import com.autoflex.dto.CreateProductRequest
import com.autoflex.dto.ProductDto
import com.autoflex.dto.UpdateProductRequest
import com.autoflex.entity.Product
import com.autoflex.exception.BadRequestException
import com.autoflex.exception.NotFoundException
import com.autoflex.repository.ProductRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.math.BigDecimal
import java.util.*

@ApplicationScoped
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun getAll(): List<ProductDto> {
        return productRepository.listAll().map { it.toDto() }
    }
    
    fun getByCode(code: String): ProductDto {
        val product = productRepository.findByCode(code)
            ?: throw NotFoundException("Product with code $code not found")
        return product.toDto()
    }
    
    @Transactional
    fun create(request: CreateProductRequest): ProductDto {
        val code = generateProductCode()
        
        if (productRepository.existsByCode(code)) {
            throw BadRequestException("Product code $code already exists")
        }
        
        val product = Product().apply {
            this.code = code
            this.name = request.name
            this.value = request.value
        }
        
        productRepository.persist(product)
        return product.toDto()
    }
    
    @Transactional
    fun update(code: String, request: UpdateProductRequest): ProductDto {
        val product = productRepository.findByCode(code)
            ?: throw NotFoundException("Product with code $code not found")
        
        request.name?.let { product.name = it }
        request.value?.let { product.value = it }
        
        productRepository.persist(product)
        return product.toDto()
    }
    
    @Transactional
    fun delete(code: String) {
        val product = productRepository.findByCode(code)
            ?: throw NotFoundException("Product with code $code not found")
        
        productRepository.delete(product)
    }
    
    private fun generateProductCode(): String {
        val count = productRepository.count()
        return "P${String.format("%03d", count + 1)}"
    }
    
    private fun Product.toDto(): ProductDto {
        return ProductDto(
            code = this.code,
            name = this.name,
            value = this.value
        )
    }
}
