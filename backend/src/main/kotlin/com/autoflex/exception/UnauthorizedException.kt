package com.autoflex.exception

/**
 * Exception thrown when authentication or authorization fails.
 */
class UnauthorizedException(message: String) : RuntimeException(message)
