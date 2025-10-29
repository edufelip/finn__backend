package com.finn.security.firebase

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FirebaseAppCheckVerifier : AppCheckVerifier {
    private val logger = LoggerFactory.getLogger(FirebaseAppCheckVerifier::class.java)

    override fun verify(token: String): Boolean {
        logger.warn("Firebase App Check verification is not yet implemented on the JVM; accepting token placeholder.")
        return true
    }
}
