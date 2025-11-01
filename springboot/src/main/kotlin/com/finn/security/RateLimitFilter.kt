package com.finn.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitFilter(
    @Value("\${rateLimit.rpm:60}") private val rpm: Long,
    @Value("\${security.disableAuth:false}") private val disableAuth: Boolean,
) : OncePerRequestFilter() {
    private data class Bucket(var windowStart: Long, var tokens: Long)

    private val buckets = ConcurrentHashMap<String, Bucket>()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        val key =
            SecurityContextHolder.getContext().authentication?.name
                ?: request.getHeader("X-Forwarded-For")?.split(',')?.first()?.trim()
                ?: request.remoteAddr

        if (!disableAuth) {
            val now = Instant.now().epochSecond
            val bucket =
                buckets.compute(key) { _, b ->
                    val window = now / 60
                    val current = b ?: Bucket(window, rpm)
                    if (current.windowStart != window) Bucket(window, rpm) else current
                }!!

            if (bucket.tokens <= 0) {
                response.status = 429
                response.contentType = "application/json"
                response.writer.write("{\"error\":\"Too Many Requests\"}")
                return
            }
            bucket.tokens -= 1
        }

        chain.doFilter(request, response)
    }
}
