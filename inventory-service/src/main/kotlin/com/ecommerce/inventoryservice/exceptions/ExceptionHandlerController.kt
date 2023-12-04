package com.ecommerce.inventoryservice.exceptions

import com.ecommerce.inventoryservice.dto.responses.ErrorResponse
import com.ecommerce.inventoryservice.utils.createErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerController {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(InventoryNotFoundException::class)
    fun handleInventoryNotFoundException(
        inventoryNotFoundException: InventoryNotFoundException
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = inventoryNotFoundException.message?.let {
            logger.info(it)
            createErrorResponse(HttpStatus.NOT_FOUND, it)
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }
}