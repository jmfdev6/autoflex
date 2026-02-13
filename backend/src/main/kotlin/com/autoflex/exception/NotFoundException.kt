package com.autoflex.exception

class NotFoundException(
    message: String,
    val errorCode: String = "NOT_FOUND"
) : RuntimeException(message)
