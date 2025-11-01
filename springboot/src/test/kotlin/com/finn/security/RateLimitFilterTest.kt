package com.finn.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class RateLimitFilterTest {
    @Test
    fun `rate limiter allows up to rpm then returns 429`() {
        val filter = RateLimitFilter(rpm = 2, disableAuth = false)
        var proceedCount = 0
        val chain = FilterChain { _: ServletRequest, _: ServletResponse -> proceedCount++ }

        val req1 = MockHttpServletRequest().apply { remoteAddr = "1.2.3.4" }
        val resp1 = MockHttpServletResponse()
        filter.doFilter(req1, resp1, chain)
        assertEquals(200, resp1.status)

        val req2 = MockHttpServletRequest().apply { remoteAddr = "1.2.3.4" }
        val resp2 = MockHttpServletResponse()
        filter.doFilter(req2, resp2, chain)
        assertEquals(200, resp2.status)

        val req3 = MockHttpServletRequest().apply { remoteAddr = "1.2.3.4" }
        val resp3 = MockHttpServletResponse()
        filter.doFilter(req3, resp3, chain)
        assertEquals(429, resp3.status)

        assertEquals(2, proceedCount)
    }
}
