package com.finn.storage

import com.finn.exception.UploadValidationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.security.SecureRandom

@Service
class LocalFileStorageService(
    @Value("\${app.upload.dir:../public}") private val uploadDir: String,
) : StorageService {
    private val random = SecureRandom()
    private val baseDir = File(uploadDir)

    init {
        if (!baseDir.exists()) baseDir.mkdirs()
    }

    override fun storePngWithChecks(file: MultipartFile): String {
        if (file.size > 1024 * 300) throw UploadValidationException("File too large")
        val original = (file.originalFilename ?: "").lowercase()
        if (!original.endsWith(".png")) throw UploadValidationException("File must be .png")
        val name = randomHex(16) + ".png"
        val dest = File(baseDir, name)
        file.inputStream.use { input ->
            Files.copy(input, dest.toPath())
        }
        return name
    }

    override fun deleteIfExists(filename: String?) {
        if (filename.isNullOrBlank()) return
        val f = File(baseDir, filename)
        if (f.exists()) f.delete()
    }

    private fun randomHex(bytes: Int): String {
        val arr = ByteArray(bytes)
        random.nextBytes(arr)
        return arr.joinToString("") { String.format("%02x", it) }
    }
}
