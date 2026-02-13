package com.autoflex.resource

import com.autoflex.dto.ApiResponse
import com.autoflex.dto.ConfirmProductionRequest
import com.autoflex.dto.ProductionResponseDto
import com.autoflex.exception.BadRequestException
import com.autoflex.exception.ConcurrencyException
import com.autoflex.exception.InsufficientStockException
import com.autoflex.service.ProductionService
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

@Path("/productions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Productions", description = "API para criar e confirmar produções")
class ProductionsResource @Inject constructor(
    private val productionService: ProductionService
) {

    @POST
    @Operation(
        summary = "Criar produção",
        description = "Cria uma nova produção em estado PENDING com a lista de itens (productCode, quantity). Use POST /productions/{id}/confirm para confirmar e consumir estoque."
    )
    @APIResponse(
        responseCode = "201",
        description = "Produção criada com sucesso",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "400",
        description = "Dados inválidos"
    )
    fun create(@Valid request: ConfirmProductionRequest): Response {
        val production = productionService.createProduction(request)
        return Response.status(Response.Status.CREATED).entity(
            ApiResponse(
                success = true,
                data = production,
                message = "Production created successfully"
            )
        ).build()
    }

    @POST
    @Path("/{id}/confirm")
    @Operation(
        summary = "Confirmar produção",
        description = "Confirma a produção pelo id, consumindo estoque de matérias-primas. A produção deve estar em PENDING."
    )
    @APIResponse(
        responseCode = "200",
        description = "Produção confirmada (pode conter itens com falha)",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "400",
        description = "Produção já confirmada ou dados inválidos"
    )
    @APIResponse(
        responseCode = "404",
        description = "Produção não encontrada"
    )
    @APIResponse(
        responseCode = "409",
        description = "Conflito de concorrência"
    )
    fun confirm(@PathParam("id") id: Long): Response {
        return try {
            val result = productionService.confirmProductionById(id)
            val status = when {
                result.failureCount == 0 -> Response.Status.OK
                result.successCount == 0 -> Response.Status.BAD_REQUEST
                else -> Response.Status.ACCEPTED
            }
            Response.status(status).entity(
                ApiResponse(
                    success = result.failureCount == 0,
                    data = result,
                    message = if (result.failureCount == 0) {
                        "Production confirmed successfully"
                    } else if (result.successCount == 0) {
                        "All production items failed"
                    } else {
                        "${result.successCount} items confirmed, ${result.failureCount} items failed"
                    }
                )
            ).build()
        } catch (e: ConcurrencyException) {
            Response.status(Response.Status.CONFLICT).entity(
                ApiResponse(
                    success = false,
                    data = null,
                    message = e.message ?: "Concurrency conflict occurred"
                )
            ).build()
        } catch (e: InsufficientStockException) {
            Response.status(Response.Status.BAD_REQUEST).entity(
                ApiResponse(
                    success = false,
                    data = null,
                    message = e.message ?: "Insufficient stock"
                )
            ).build()
        } catch (e: BadRequestException) {
            Response.status(Response.Status.BAD_REQUEST).entity(
                ApiResponse(
                    success = false,
                    data = null,
                    message = e.message ?: "Bad request"
                )
            ).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                ApiResponse(
                    success = false,
                    data = null,
                    message = "An error occurred: ${e.message}"
                )
            ).build()
        }
    }
}
