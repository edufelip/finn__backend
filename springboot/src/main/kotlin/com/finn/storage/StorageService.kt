package com.finn.storage

import org.springframework.web.multipart.MultipartFile

interface StorageService {
    fun storePngWithChecks(file: MultipartFile): String
    fun deleteIfExists(filename: String?)
}

