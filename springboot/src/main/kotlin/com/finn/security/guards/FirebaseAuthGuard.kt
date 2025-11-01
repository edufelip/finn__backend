package com.finn.security.guards

import com.finn.security.firebase.IdTokenVerifier
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component

@Component
class FirebaseAuthGuard(
    @Value("\${security.disableAuth:false}") private val disableAuth: Boolean,
    private val verifier: IdTokenVerifier,
) : Guard {
    override fun check(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Boolean {
        if (disableAuth) return true
        val authHeader = request.getHeader("Authorization")
        if (authHeader?.startsWith("Bearer ") == true) {
            val token = authHeader.substringAfter("Bearer ").trim()
            val uid = verifier.verifyAndGetUid(token)
            return if (uid != null) {
                val principal = User(uid, "", emptyList())
                val authentication = UsernamePasswordAuthenticationToken(principal, token, principal.authorities)
                SecurityContextHolder.getContext().authentication = authentication
                true
            } else {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = "application/json"
                response.writer.write("{\"error\":\"Unauthorized\",\"message\":\"Invalid Firebase token\"}")
                false
            }
        }
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.writer.write("{\"error\":\"Unauthorized\",\"message\":\"Missing Authorization header\"}")
        return false
    }
}
