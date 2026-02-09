package com.autoflex.resource

import com.autoflex.dto.ApiResponse
import com.autoflex.dto.ConfirmProductionRequest
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

@Path("/production")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Production", description = "API para sugestões de produção baseadas no estoque disponível")
class ProductionResource @Inject constructor(
    private val productionService: ProductionService
) {
    
    @GET
    @Path("/suggestions")
    @Operation(
        summary = "Obter sugestões de produção",
        description = "Retorna uma lista de produtos que podem ser produzidos com as matérias-primas disponíveis em estoque. " +
                "Os produtos são priorizados por valor (maior para menor). " +
                "O algoritmo considera que uma matéria-prima pode ser usada em múltiplos produtos."
    )
    @APIResponse(
        responseCode = "200",
        description = "Sugestões de produção calculadas com sucesso",
        content = [Content(schema = Schema())]
    )
    fun getProductionSuggestions(): Response {
        val summary = productionService.getProductionSuggestions()
        return Response.ok(
            ApiResponse(
                success = true,
                data = summary
            )
        ).build()
    }
    
    @POST
    @Path("/confirm")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Confirmar produção",
        description = "Confirma a produção de produtos, atualizando o estoque de matérias-primas. " +
                "Utiliza locks pessimistas e versioning otimista para garantir consistência em ambiente multiusuário. " +
                "Retorna resultado detalhado de cada item, incluindo sucessos e falhas."
    )
    @APIResponse(
        responseCode = "200",
        description = "Produção confirmada (pode conter itens com falha)",
        content = [Content(schema = Schema())]
    )
    @APIResponse(
        responseCode = "400",
        description = "Dados inválidos fornecidos"
    )
    @APIResponse(
        responseCode = "409",
        description = "Conflito de concorrência detectado"
    )
    fun confirmProduction(@Valid request: ConfirmProductionRequest): Response {
        return try {
            val result = productionService.confirmProduction(request)
            
            val status = if (result.failureCount == 0) {
                Response.Status.OK
            } else if (result.successCount == 0) {
                Response.Status.BAD_REQUEST
            } else {
                Response.Status.ACCEPTED // Parcialmente processado
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
