package com.ecommerce.inventoryservice.controllers

import com.ecommerce.inventoryservice.constants.MessageResponses
import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.dto.responses.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
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

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}