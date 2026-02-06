package com.autoflex.resource

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/health")
class HealthResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun health(): Response {
        return Response.ok(
            mapOf(
                "status" to "UP",
                "service" to "autoflex-backend"
            )
        ).build()
    }
}
