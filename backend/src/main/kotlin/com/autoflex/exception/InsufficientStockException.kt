package com.autoflex.exception

class InsufficientStockException(
    message: String,
    val rawMaterialCode: String? = null,
    val available: java.math.BigDecimal? = null,
    val requested: java.math.BigDecimal? = null
) : RuntimeException(message)
