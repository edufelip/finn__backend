package com.finn.security.guards

import com.finn.security.RequestGuardsFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class RequestGuardsFilterTest {
    @Test
    fun `stops chain when a guard fails`() {
        val pass =
            object : Guard {
                override fun check(
                    request: jakarta.servlet.http.HttpServletRequest,
                    response: jakarta.servlet.http.HttpServletResponse,
                ) = true
            }
        val fail =
            object : Guard {
                override fun check(
                    request: jakarta.servlet.http.HttpServletRequest,
                    response: jakarta.servlet.http.HttpServletResponse,
                ): Boolean {
                    response.status = 418
                    return false
                }
            }
        var proceeded = false
        val chain = FilterChain { _: ServletRequest, _: ServletResponse -> proceeded = true }

        val filter = RequestGuardsFilter(listOf(pass, fail, pass))
        val req = MockHttpServletRequest()
        val resp = MockHttpServletResponse()

        filter.doFilter(req, resp, chain)

        assertFalse(proceeded)
        assertEquals(418, resp.status)
    }
}
