package com.github.gnobroga.file_storage_api

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class SendEmailEventListener(
    val mailerSender: JavaMailSender,
) : ApplicationListener<SendEmailEvent> {

    private val logger = LoggerFactory.getLogger(SendEmailEventListener::class.simpleName)

    override fun onApplicationEvent(event: SendEmailEvent) {
        val (to, subject, body) = event.email
        val message = SimpleMailMessage().apply {
            from = "noreplay@gmail.com"
            setTo(to)
            this.subject = subject
            text = body
        }
        mailerSender.send(message)
        logger.info("Email was sending to $to with content $body")
    }
}