package com.github.gnobroga.file_storage_api

import jakarta.mail.Message
import jakarta.servlet.http.HttpServletRequest
import org.apache.tomcat.util.http.fileupload.FileUtils
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Controller
import org.springframework.util.ResourceUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.servlet.function.ServerRequest.Headers
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@Controller
@RequestMapping("/api/files")
class FileStorageController(
    fileStorageProperties: FileStorageProperties,
    val eventPublisher: ApplicationEventPublisher,
) {
    private val fileStorageLocation: Path = Path.of(fileStorageProperties.uploadDir)
        .toAbsolutePath().normalize()

    @PostMapping("/upload")
    fun uploadFile(@RequestPart file: MultipartFile): ResponseEntity<Map<String, String>> {
        try {
            val fileName = StringUtils.cleanPath(file.originalFilename!!)
            file.transferTo(fileStorageLocation.resolve(fileName))
            val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/{fileName}")
                .buildAndExpand(fileName)
                .toUriString()

            eventPublisher.publishEvent(SendEmailEvent(this as Object, Email(to="test@email.com", subject="no subject", body="no body")))

            return ResponseEntity.ok(mapOf(
                "message" to "upload completed!",
                "downloadLink" to fileDownloadUri
            ))
        } catch(error: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/download/{fileName:.+}")
    fun downloadFile(@PathVariable fileName: String, httpServetletRequest: HttpServletRequest): ResponseEntity<ByteArray> {
        try {
            val filePath = fileStorageLocation.resolve(fileName).normalize()
            val mimeType = httpServetletRequest.servletContext.getMimeType(fileName) ?:
                MediaType.APPLICATION_OCTET_STREAM_VALUE
            return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
                .contentType(MediaType.valueOf(mimeType))
                .body(Files.readAllBytes(filePath))
        } catch (error: IOException) {
            return ResponseEntity.badRequest().build()
        }
    }
}