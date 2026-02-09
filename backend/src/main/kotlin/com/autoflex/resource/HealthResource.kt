package com.autoflex.resource

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Liveness
import org.eclipse.microprofile.health.Readiness
import org.jboss.logging.Logger
import java.time.Instant
import javax.sql.DataSource
import java.sql.Connection

@Path("/health")
@ApplicationScoped
class HealthResource {
    
    private val logger = Logger.getLogger(HealthResource::class.java)
    
    @Inject
    lateinit var dataSource: DataSource
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun health(): Response {
        val checks = mutableMapOf<String, Any>()
        var overallStatus = "UP"
        
        // Database health check
        try {
            dataSource.connection.use { connection ->
                val isValid = connection.isValid(5) // 5 second timeout
                checks["database"] = mapOf(
                    "status" to if (isValid) "UP" else "DOWN",
                    "connected" to isValid
                )
                if (!isValid) overallStatus = "DOWN"
            }
        } catch (e: Exception) {
            logger.error("Database health check failed", e)
            checks["database"] = mapOf(
                "status" to "DOWN",
                "error" to e.message
            )
            overallStatus = "DOWN"
        }
        
        // Application info
        checks["application"] = mapOf(
            "status" to "UP",
            "name" to "autoflex-backend",
            "timestamp" to Instant.now().toString()
        )
        
        val statusCode = if (overallStatus == "UP") Response.Status.OK else Response.Status.SERVICE_UNAVAILABLE
        
        return Response.status(statusCode).entity(
            mapOf(
                "status" to overallStatus,
                "checks" to checks
            )
        ).build()
    }
}

/**
 * Liveness probe - indicates if the application is running.
 */
@Liveness
@ApplicationScoped
class LivenessCheck : HealthCheck {
    override fun call(): HealthCheckResponse {
        return HealthCheckResponse.up("autoflex-backend-liveness")
    }
}

/**
 * Readiness probe - indicates if the application is ready to serve traffic.
 */
@Readiness
@ApplicationScoped
class ReadinessCheck @Inject constructor(
    private val dataSource: DataSource
) : HealthCheck {
    override fun call(): HealthCheckResponse {
        return try {
            dataSource.connection.use { connection ->
                val isValid = connection.isValid(5)
                if (isValid) {
                    HealthCheckResponse.up("autoflex-backend-readiness")
                } else {
                    HealthCheckResponse.builder()
                        .name("autoflex-backend-readiness")
                        .down()
                        .withData("database", "connection invalid")
                        .build()
                }
            }
        } catch (e: Exception) {
            HealthCheckResponse.builder()
                .name("autoflex-backend-readiness")
                .down()
                .withData("database", "connection failed: ${e.message}")
                .build()
        }
    }
}

