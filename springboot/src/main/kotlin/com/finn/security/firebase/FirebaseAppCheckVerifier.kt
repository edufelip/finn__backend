package com.finn.security.firebase

import com.google.firebase.appcheck.FirebaseAppCheck
import org.springframework.stereotype.Component

@Component
class FirebaseAppCheckVerifier : AppCheckVerifier {
    override fun verify(token: String): Boolean = try {
        FirebaseAppCheck.getInstance().verifyToken(token)
        true
    } catch (_: Exception) {
        false
    }
}

