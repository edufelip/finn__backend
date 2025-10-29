package com.finn.security.firebase

fun interface IdTokenVerifier {
    /**
     * @return UID if valid, null otherwise
     */
    fun verifyAndGetUid(token: String): String?
}
