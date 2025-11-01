package com.finn.storage

import com.finn.exception.UploadValidationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.mock.web.MockMultipartFile
import java.io.File
import java.nio.file.Path

class LocalFileStorageServiceTest {
    @TempDir
    lateinit var tmp: Path

    @Test
    fun `stores png under limit`() {
        val svc = LocalFileStorageService(uploadDir = tmp.toString())
        val data = ByteArray(1024) { 0x01 }
        val file = MockMultipartFile("f", "a.png", "image/png", data)
        val name = svc.storePngWithChecks(file)
        assertTrue(File(tmp.toFile(), name).exists())
        svc.deleteIfExists(name)
        assertFalse(File(tmp.toFile(), name).exists())
    }

    @Test
    fun `rejects wrong extension`() {
        val svc = LocalFileStorageService(uploadDir = tmp.toString())
        val file = MockMultipartFile("f", "a.jpg", "image/jpeg", ByteArray(1000))
        val ex = assertThrows(UploadValidationException::class.java) { svc.storePngWithChecks(file) }
        assertEquals("File must be .png", ex.message)
    }

    @Test
    fun `rejects too large`() {
        val svc = LocalFileStorageService(uploadDir = tmp.toString())
        val file = MockMultipartFile("f", "a.png", "image/png", ByteArray(350 * 1024))
        val ex = assertThrows(UploadValidationException::class.java) { svc.storePngWithChecks(file) }
        assertEquals("File too large", ex.message)
    }
}
