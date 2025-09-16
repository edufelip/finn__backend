package com.finn.security.guards

import com.finn.security.firebase.AppCheckVerifier
import com.finn.security.firebase.IdTokenVerifier
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class FirebaseGuardsTest {

    @Test
    fun `app check guard accepts valid and rejects invalid`() {
        val okVerifier = AppCheckVerifier { true }
        val failVerifier = AppCheckVerifier { false }

        val req = MockHttpServletRequest()
        req.addHeader("X-Firebase-AppCheck", "token")

        val resp1 = MockHttpServletResponse()
        val guardOk = FirebaseAppCheckGuard(requireAppCheck = true, disableAuth = false, verifier = okVerifier)
        assertTrue(guardOk.check(req, resp1))

        val resp2 = MockHttpServletResponse()
        val guardFail = FirebaseAppCheckGuard(requireAppCheck = true, disableAuth = false, verifier = failVerifier)
        assertFalse(guardFail.check(req, resp2))
        assertEquals(403, resp2.status)
    }

    @Test
    fun `auth guard accepts valid token and sets context, rejects invalid`() {
        val okVerifier = IdTokenVerifier { "uid-123" }
        val failVerifier = IdTokenVerifier { null }

        val req = MockHttpServletRequest()
        req.addHeader("Authorization", "Bearer abc")

        val resp1 = MockHttpServletResponse()
        val guardOk = FirebaseAuthGuard(disableAuth = false, verifier = okVerifier)
        assertTrue(guardOk.check(req, resp1))
        assertEquals(200, resp1.status)

        val resp2 = MockHttpServletResponse()
        val guardFail = FirebaseAuthGuard(disableAuth = false, verifier = failVerifier)
        assertFalse(guardFail.check(req, resp2))
        assertEquals(401, resp2.status)
    }
}

