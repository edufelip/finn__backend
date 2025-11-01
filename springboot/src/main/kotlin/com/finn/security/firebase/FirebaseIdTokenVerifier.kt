package com.finn.security.firebase

import com.google.firebase.auth.FirebaseAuth
import org.springframework.stereotype.Component

@Component
class FirebaseIdTokenVerifier : IdTokenVerifier {
    override fun verifyAndGetUid(token: String): String? =
        try {
            val decoded = FirebaseAuth.getInstance().verifyIdToken(token)
            decoded.uid
        } catch (_: Exception) {
            null
        }
}
