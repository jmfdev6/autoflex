package com.autoflex

import jakarta.ws.rs.core.Application

// O prefixo /api é configurado via quarkus.resteasy-reactive.path no application.properties
// Não é necessário usar @ApplicationPath quando usamos quarkus.resteasy-reactive.path
class AutoflexApplication : Application() {
    // Não sobrescrever getClasses() - o Quarkus descobre automaticamente os recursos REST
}
