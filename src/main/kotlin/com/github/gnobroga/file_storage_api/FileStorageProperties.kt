package com.github.gnobroga.file_storage_api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "file", ignoreUnknownFields = true)
class FileStorageProperties {

    var uploadDir: String? = null
}