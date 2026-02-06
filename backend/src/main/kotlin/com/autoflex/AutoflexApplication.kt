package com.autoflex

import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.core.Application

@ApplicationPath("/api")
class AutoflexApplication : Application() {
    // Não sobrescrever getClasses() - o Quarkus descobre automaticamente os recursos REST
    // O CorsFilter é registrado automaticamente via @Provider
}
