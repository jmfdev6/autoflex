package com.autoflex.resource

import com.autoflex.dto.ApiResponse
import com.autoflex.dto.CreateProductRequest
import com.autoflex.dto.PageRequest
import com.autoflex.dto.PageResponse
import com.autoflex.dto.ProductDto
import com.autoflex.dto.UpdateProductRequest
import com.autoflex.service.ProductService
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Products", description = "API para gerenciamento de produtos")
class ProductsResource @Inject constructor(
    private val productService: ProductService
) {
    
    @GET
    @Operation(
        summary = "Listar produtos",
        description = "Retorna uma lista paginada de produtos. Use os parâmetros page, size e sort para paginação."
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de produtos retornada com sucesso",
        content = [Content(schema = Schema(implementation = ApiResponse::class))]
    )
    fun getAll(
        @BeanParam pageRequest: PageRequest
    ): Response {
        // Se não especificou paginação (valores padrão), retornar lista simples para compatibilidade
        val usePagination = pageRequest.page > 0 || pageRequest.size != 20 || pageRequest.sort != "code"
        
        return if (usePagination) {
            val pageResponse = productService.getAllPaginated(pageRequest)
            Response.ok(
                ApiResponse(
                    success = true,
                    data = pageResponse
                )
            ).build()
        } else {
            // Compatibilidade: retornar lista simples se não usar paginação
            val products = productService.getAll()
            Response.ok(
                ApiResponse(
                    success = true,
                    data = products
                )
            ).build()
        }
    }
    
    @GET
    @Path("/{code}")
    @Operation(
        summary = "Buscar produto por código",
        description = "Retorna um produto específico baseado no código fornecido"
    )
    @APIResponse(
        responseCode = "200",
        description = "Produto encontrado",
        content = [Content(schema = Schema(implementation = ApiResponse::class))]
    )
    @APIResponse(
        responseCode = "404",
        description = "Produto não encontrado"
    )
    fun getByCode(@PathParam("code") code: String): Response {
        val product = productService.getByCode(code)
        return Response.ok(
            ApiResponse(
                success = true,
                data = product
            )
        ).build()
    }
    
    @POST
    @Operation(
        summary = "Criar novo produto",
        description = "Cria um novo produto no sistema. O código é gerado automaticamente."
    )
    @APIResponse(
        responseCode = "201",
        description = "Produto criado com sucesso",
        content = [Content(schema = Schema(implementation = ApiResponse::class))]
    )
    @APIResponse(
        responseCode = "400",
        description = "Dados inválidos fornecidos"
    )
    fun create(@Valid request: CreateProductRequest): Response {
        val product = productService.create(request)
        return Response.status(Response.Status.CREATED).entity(
            ApiResponse(
                success = true,
                data = product,
                message = "Product created successfully"
            )
        ).build()
    }
    
    @PUT
    @Path("/{code}")
    @Operation(
        summary = "Atualizar produto",
        description = "Atualiza os dados de um produto existente. Campos não fornecidos não serão alterados."
    )
    @APIResponse(
        responseCode = "200",
        description = "Produto atualizado com sucesso",
        content = [Content(schema = Schema(implementation = ApiResponse::class))]
    )
    @APIResponse(
        responseCode = "404",
        description = "Produto não encontrado"
    )
    @APIResponse(
        responseCode = "400",
        description = "Dados inválidos fornecidos"
    )
    fun update(
        @PathParam("code") code: String,
        @Valid request: UpdateProductRequest
    ): Response {
        val product = productService.update(code, request)
        return Response.ok(
            ApiResponse(
                success = true,
                data = product,
                message = "Product updated successfully"
            )
        ).build()
    }
    
    @DELETE
    @Path("/{code}")
    @Operation(
        summary = "Deletar produto",
        description = "Remove um produto do sistema. Também remove todas as associações com matérias-primas."
    )
    @APIResponse(
        responseCode = "200",
        description = "Produto deletado com sucesso",
        content = [Content(schema = Schema(implementation = ApiResponse::class))]
    )
    @APIResponse(
        responseCode = "404",
        description = "Produto não encontrado"
    )
    fun delete(@PathParam("code") code: String): Response {
        productService.delete(code)
        return Response.ok(
            ApiResponse(
                success = true,
                data = null,
                message = "Product deleted successfully"
            )
        ).build()
    }
}
