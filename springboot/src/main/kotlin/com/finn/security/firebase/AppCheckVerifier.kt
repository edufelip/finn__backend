package com.finn.security.firebase

interface AppCheckVerifier {
    fun verify(token: String): Boolean
}

