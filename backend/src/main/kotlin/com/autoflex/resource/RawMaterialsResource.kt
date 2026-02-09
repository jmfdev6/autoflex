package com.autoflex.resource

import com.autoflex.dto.ApiResponse
import com.autoflex.dto.CreateRawMaterialRequest
import com.autoflex.dto.PageRequest
import com.autoflex.dto.RawMaterialDto
import com.autoflex.dto.UpdateRawMaterialRequest
import com.autoflex.service.RawMaterialService
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

@Path("/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Raw Materials", description = "API para gerenciamento de matérias-primas")
class RawMaterialsResource @Inject constructor(
    private val rawMaterialService: RawMaterialService
) {
    
    @GET
    @Operation(
        summary = "Listar matérias-primas",
        description = "Retorna uma lista paginada de matérias-primas. Use os parâmetros page, size e sort para paginação."
    )
    @APIResponse(
        responseCode = "200",
        description = "Lista de matérias-primas retornada com sucesso",
        content = [Content(schema = Schema())]
    )
    fun getAll(
        @BeanParam pageRequest: PageRequest
    ): Response {
        // Se não especificou paginação (valores padrão), retornar lista simples para compatibilidade
        val usePagination = pageRequest.page > 0 || pageRequest.size != 20 || pageRequest.sort != "code"
        
        return if (usePagination) {
            val pageResponse = rawMaterialService.getAllPaginated(pageRequest)
            Response.ok(
                ApiResponse(
                    success = true,
                    data = pageResponse
                )
            ).build()
        } else {
            // Compatibilidade: retornar lista simples se não usar paginação
            val rawMaterials = rawMaterialService.getAll()
            Response.ok(
                ApiResponse(
                    success = true,
                    data = rawMaterials
                )
            ).build()
        }
    }
    
    @GET
    @Path("/{code}")
    @Operation(
        summary = "Buscar matéria-prima por código",
        description = "Retorna uma matéria-prima específica baseada no código fornecido"
    )
    @APIResponse(
        responseCode = "200",
        description = "Matéria-prima encontrada",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "404",
        description = "Matéria-prima não encontrada"
    )
    fun getByCode(@PathParam("code") code: String): Response {
        val rawMaterial = rawMaterialService.getByCode(code)
        return Response.ok(
            ApiResponse(
                success = true,
                data = rawMaterial
            )
        ).build()
    }
    
    @POST
    @Operation(
        summary = "Criar nova matéria-prima",
        description = "Cria uma nova matéria-prima no sistema. O código é gerado automaticamente."
    )
    @APIResponse(
        responseCode = "201",
        description = "Matéria-prima criada com sucesso",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "400",
        description = "Dados inválidos fornecidos"
    )
    fun create(@Valid request: CreateRawMaterialRequest): Response {
        val rawMaterial = rawMaterialService.create(request)
        return Response.status(Response.Status.CREATED).entity(
            ApiResponse(
                success = true,
                data = rawMaterial,
                message = "Raw material created successfully"
            )
        ).build()
    }
    
    @PUT
    @Path("/{code}")
    @Operation(
        summary = "Atualizar matéria-prima",
        description = "Atualiza os dados de uma matéria-prima existente. Campos não fornecidos não serão alterados."
    )
    @APIResponse(
        responseCode = "200",
        description = "Matéria-prima atualizada com sucesso",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "404",
        description = "Matéria-prima não encontrada"
    )
    @APIResponse(
        responseCode = "400",
        description = "Dados inválidos fornecidos"
    )
    fun update(
        @PathParam("code") code: String,
        @Valid request: UpdateRawMaterialRequest
    ): Response {
        val rawMaterial = rawMaterialService.update(code, request)
        return Response.ok(
            ApiResponse(
                success = true,
                data = rawMaterial,
                message = "Raw material updated successfully"
            )
        ).build()
    }
    
    @DELETE
    @Path("/{code}")
    @Operation(
        summary = "Deletar matéria-prima",
        description = "Remove uma matéria-prima do sistema. Também remove todas as associações com produtos."
    )
    @APIResponse(
        responseCode = "200",
        description = "Matéria-prima deletada com sucesso",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "404",
        description = "Matéria-prima não encontrada"
    )
    fun delete(@PathParam("code") code: String): Response {
        rawMaterialService.delete(code)
        return Response.ok(
            ApiResponse(
                success = true,
                data = null,
                message = "Raw material deleted successfully"
            )
        ).build()
    }
}
