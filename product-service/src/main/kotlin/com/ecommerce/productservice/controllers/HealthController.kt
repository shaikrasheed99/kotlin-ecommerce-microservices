package com.ecommerce.productservice.controllers

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.dto.response.SuccessResponse
import com.ecommerce.productservice.utils.createSuccessResponse
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

        logger.info("health API is OK")

        return ResponseEntity.ok(successResponse)
    }
}