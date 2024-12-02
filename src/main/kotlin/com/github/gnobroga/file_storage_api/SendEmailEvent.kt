package com.github.gnobroga.file_storage_api

import org.springframework.context.ApplicationEvent


data class Email(val to: String, val subject: String, val body: String)

class SendEmailEvent(source: Object, val email: Email) : ApplicationEvent(source)
