package com.autoflex.resource

import com.autoflex.dto.ApiResponse
import com.autoflex.service.ProductionService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag

@Path("/production-suggestions")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Production Suggestions", description = "Sugestões de produção baseadas no estoque disponível")
class ProductionSuggestionsResource @Inject constructor(
    private val productionService: ProductionService
) {

    @GET
    @Operation(
        summary = "Listar sugestões de produção",
        description = "Retorna produtos que podem ser produzidos com as matérias-primas em estoque, priorizados por valor."
    )
    @APIResponse(
        responseCode = "200",
        description = "Sugestões calculadas com sucesso",
        content = [Content(schema = Schema())]
    )
    fun getSuggestions(): Response {
        val summary = productionService.getProductionSuggestions()
        return Response.ok(
            ApiResponse(
                success = true,
                data = summary
            )
        ).build()
    }
}
