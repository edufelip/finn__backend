package com.finn.security.guards

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppHeaderGuard(
    @Value("\${security.requireAppHeader:true}") private val requireAppHeader: Boolean,
    @Value("\${security.app.allowedPackage:com.edufelip.finn}") private val allowedPackage: String,
    @Value("\${security.app.allowedUserAgentContains:okhttp}") private val allowedUserAgentContains: String,
    @Value("\${security.disableAuth:false}") private val disableAuth: Boolean,
) : Guard {
    override fun check(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Boolean {
        if (!requireAppHeader || disableAuth) return true
        val appHeader = request.getHeader("X-App-Package")
        val userAgent = request.getHeader("User-Agent") ?: ""
        val allowed = appHeader == allowedPackage || userAgent.contains(allowedUserAgentContains, ignoreCase = true)
        if (!allowed) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Forbidden\",\"message\":\"Invalid client\"}")
            return false
        }
        return true
    }
}
