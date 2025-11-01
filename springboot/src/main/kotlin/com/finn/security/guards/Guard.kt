package com.finn.security.guards

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface Guard {
    fun check(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Boolean
}
