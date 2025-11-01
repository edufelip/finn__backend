package com.finn.security

import com.finn.security.guards.Guard
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestGuardsFilter(
    private val guards: List<Guard>,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        for (guard in guards) {
            if (!guard.check(request, response)) {
                return
            }
        }
        chain.doFilter(request, response)
    }
}
