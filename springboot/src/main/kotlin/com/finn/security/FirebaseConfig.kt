package com.finn.security

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.charset.StandardCharsets

@Configuration
class FirebaseConfig {
    init {
        if (FirebaseApp.getApps().isEmpty()) {
            val options = buildOptions()
            if (options != null) {
                FirebaseApp.initializeApp(options)
            }
        }
    }

    private fun buildOptions(): FirebaseOptions? {
        return try {
            val credentials: GoogleCredentials? =
                when {
                    // 1) JSON content in env var FIREBASE_SERVICE_ACCOUNT (base64 or raw JSON)
                    System.getenv("FIREBASE_SERVICE_ACCOUNT") != null -> {
                        val raw = System.getenv("FIREBASE_SERVICE_ACCOUNT")
                        val decoded =
                            try {
                                java.util.Base64.getDecoder().decode(raw)
                            } catch (_: Exception) {
                                raw!!.toByteArray(StandardCharsets.UTF_8)
                            }
                        GoogleCredentials.fromStream(ByteArrayInputStream(decoded))
                    }
                    // 2) GOOGLE_APPLICATION_CREDENTIALS points to JSON file
                    System.getenv("GOOGLE_APPLICATION_CREDENTIALS") != null -> {
                        GoogleCredentials.fromStream(File(System.getenv("GOOGLE_APPLICATION_CREDENTIALS")).inputStream())
                    }
                    else -> null
                }
            credentials?.let {
                FirebaseOptions.builder()
                    .setCredentials(it)
                    .build()
            }
        } catch (_: Exception) {
            null
        }
    }
}
