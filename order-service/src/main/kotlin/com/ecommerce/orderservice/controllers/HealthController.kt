package com.ecommerce.orderservice.controllers

import com.ecommerce.orderservice.constants.MessageResponses
import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.responses.Response
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/health")
    fun health(): ResponseEntity<Response> {
        val message = MessageResponses.SERVER_UP.message
        val response = Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = message,
            data = null
        )

        logger.info(message)

        return ResponseEntity.ok(response)
    }
}