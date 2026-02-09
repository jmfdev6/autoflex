package com.autoflex.resource

import com.autoflex.dto.ApiResponse
import com.autoflex.dto.CreateProductRawMaterialRequest
import com.autoflex.dto.ProductRawMaterialDto
import com.autoflex.dto.UpdateProductRawMaterialRequest
import com.autoflex.service.ProductRawMaterialService
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

@Path("/products/{productCode}/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Product Raw Materials", description = "API para gerenciamento de associações entre produtos e matérias-primas")
class ProductRawMaterialsResource @Inject constructor(
    private val productRawMaterialService: ProductRawMaterialService
) {
    
    @GET
    @Operation(
        summary = "Listar associações de um produto",
        description = "Retorna todas as matérias-primas associadas a um produto específico"
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de associações retornada com sucesso",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "404",
        description = "Produto não encontrado"
    )
    fun getByProductCode(@PathParam("productCode") productCode: String): Response {
        val associations = productRawMaterialService.getByProductCode(productCode)
        return Response.ok(
            ApiResponse(
                success = true,
                data = associations
            )
        ).build()
    }
    
    @POST
    @Operation(
        summary = "Criar associação produto-matéria-prima",
        description = "Associa uma matéria-prima a um produto com a quantidade necessária"
    )
    @APIResponse(
        responseCode = "201",
        description = "Associação criada com sucesso",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "400",
        description = "Dados inválidos ou associação já existe"
    )
    @APIResponse(
        responseCode = "404",
        description = "Produto ou matéria-prima não encontrado"
    )
    fun create(
        @PathParam("productCode") productCode: String,
        @Valid request: CreateProductRawMaterialRequest
    ): Response {
        val association = productRawMaterialService.create(productCode, request)
        return Response.status(Response.Status.CREATED).entity(
            ApiResponse(
                success = true,
                data = association,
                message = "Association created successfully"
            )
        ).build()
    }
    
    @PUT
    @Path("/{rawMaterialCode}")
    @Operation(
        summary = "Atualizar quantidade da associação",
        description = "Atualiza a quantidade de matéria-prima necessária para um produto"
    )
    @APIResponse(
        responseCode = "200",
        description = "Associação atualizada com sucesso",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "404",
        description = "Associação não encontrada"
    )
    @APIResponse(
        responseCode = "400",
        description = "Dados inválidos fornecidos"
    )
    fun update(
        @PathParam("productCode") productCode: String,
        @PathParam("rawMaterialCode") rawMaterialCode: String,
        @Valid request: UpdateProductRawMaterialRequest
    ): Response {
        val association = productRawMaterialService.update(productCode, rawMaterialCode, request)
        return Response.ok(
            ApiResponse(
                success = true,
                data = association,
                message = "Association updated successfully"
            )
        ).build()
    }
    
    @DELETE
    @Path("/{rawMaterialCode}")
    @Operation(
        summary = "Remover associação",
        description = "Remove a associação entre um produto e uma matéria-prima"
    )
    @APIResponse(
        responseCode = "200",
        description = "Associação removida com sucesso",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "404",
        description = "Associação não encontrada"
    )
    fun delete(
        @PathParam("productCode") productCode: String,
        @PathParam("rawMaterialCode") rawMaterialCode: String
    ): Response {
        productRawMaterialService.delete(productCode, rawMaterialCode)
        return Response.ok(
            ApiResponse(
                success = true,
                data = null,
                message = "Association deleted successfully"
            )
        ).build()
    }
}
