package com.autoflex.dto

data class ApiResponse<T>(
    val success: Boolean,
    val data: T,
    val message: String? = null
)

data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val errorCode: String? = null,
    val details: Map<String, Any>? = null
)
