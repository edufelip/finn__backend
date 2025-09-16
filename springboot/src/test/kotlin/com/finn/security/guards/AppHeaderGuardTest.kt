package com.finn.security.guards

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class AppHeaderGuardTest {

    @Test
    fun `allows when package header matches`() {
        val guard = AppHeaderGuard(requireAppHeader = true, allowedPackage = "com.edufelip.finn", allowedUserAgentContains = "okhttp", disableAuth = false)
        val req = MockHttpServletRequest()
        val resp = MockHttpServletResponse()
        req.addHeader("X-App-Package", "com.edufelip.finn")

        val result = guard.check(req, resp)
        assertTrue(result)
        assertEquals(200, resp.status)
    }

    @Test
    fun `allows when user agent contains okhttp`() {
        val guard = AppHeaderGuard(requireAppHeader = true, allowedPackage = "com.edufelip.finn", allowedUserAgentContains = "okhttp", disableAuth = false)
        val req = MockHttpServletRequest()
        val resp = MockHttpServletResponse()
        req.addHeader("User-Agent", "okhttp/4.0")

        val result = guard.check(req, resp)
        assertTrue(result)
        assertEquals(200, resp.status)
    }

    @Test
    fun `blocks when neither header nor ua match`() {
        val guard = AppHeaderGuard(requireAppHeader = true, allowedPackage = "com.edufelip.finn", allowedUserAgentContains = "okhttp", disableAuth = false)
        val req = MockHttpServletRequest()
        val resp = MockHttpServletResponse()

        val result = guard.check(req, resp)
        assertFalse(result)
        assertEquals(403, resp.status)
        assertTrue(resp.contentAsString.contains("Invalid client"))
    }
}

