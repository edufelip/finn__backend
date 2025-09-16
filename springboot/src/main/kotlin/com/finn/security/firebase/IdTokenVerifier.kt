package com.finn.security.firebase

interface IdTokenVerifier {
    /**
     * @return UID if valid, null otherwise
     */
    fun verifyAndGetUid(token: String): String?
}

