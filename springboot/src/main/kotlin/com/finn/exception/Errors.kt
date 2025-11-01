package com.finn.exception

open class ApiException(message: String) : RuntimeException(message)

class NotFoundException(message: String) : ApiException(message)

class ConflictException(message: String) : ApiException(message)

class BadRequestException(message: String) : ApiException(message)
