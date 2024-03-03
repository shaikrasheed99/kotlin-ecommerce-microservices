package com.ecommerce.notificationservice.controllers

import com.ecommerce.notificationservice.constants.MessageResponses
import com.ecommerce.notificationservice.constants.StatusResponses
import com.ecommerce.notificationservice.dto.responses.Response
import com.ecommerce.notificationservice.services.NotificationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notifications")
class NotificationController(private val notificationService: NotificationService) {
    @GetMapping("/recent")
    fun getRecentNotification(): ResponseEntity<Response> {
        val notification = notificationService.getRecentNotification()

        val message = MessageResponses.NOTIFICATION_FETCHED_SUCCESS.message
        val response = Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = message,
            data = notification
        )

        logger.info(message)

        return ResponseEntity.ok(response)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
