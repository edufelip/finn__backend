package com.finn.security.firebase

fun interface AppCheckVerifier {
    fun verify(token: String): Boolean
}
