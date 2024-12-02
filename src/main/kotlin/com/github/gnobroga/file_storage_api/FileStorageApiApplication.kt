package com.github.gnobroga.file_storage_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FileStorageApiApplication

fun main(args: Array<String>) {
	runApplication<FileStorageApiApplication>(*args)
}
