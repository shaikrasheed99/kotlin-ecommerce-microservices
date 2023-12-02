package com.ecommerce.orderservice.controllers

import com.ecommerce.orderservice.constants.MessageResponses
import com.ecommerce.orderservice.dto.responses.SuccessResponse
import com.ecommerce.orderservice.utils.createSuccessResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/health")
    fun health(): ResponseEntity<SuccessResponse> {
        val message = MessageResponses.SERVER_UP.message
        val successResponse = createSuccessResponse(message, null)

        logger.info(message)

        return ResponseEntity.ok(successResponse)
    }
}