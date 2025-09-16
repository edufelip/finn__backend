package com.finn.security.guards

import com.finn.security.firebase.AppCheckVerifier
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FirebaseAppCheckGuard(
    @Value("\${security.requireAppCheck:true}") private val requireAppCheck: Boolean,
    @Value("\${security.disableAuth:false}") private val disableAuth: Boolean,
    private val verifier: AppCheckVerifier
) : Guard {
    override fun check(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        if (!requireAppCheck || disableAuth) return true
        val token = request.getHeader("X-Firebase-AppCheck")
        if (token.isNullOrBlank()) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Forbidden\",\"message\":\"Missing App Check\"}")
            return false
        }
        return if (verifier.verify(token)) {
            true
        } else {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Forbidden\",\"message\":\"Invalid App Check\"}")
            false
        }
    }
}
