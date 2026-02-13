package com.autoflex.repository

import com.autoflex.entity.Production
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductionRepository : PanacheRepository<Production>
