package com.ecommerce.inventoryservice.exceptions

import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.dto.responses.Response
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
    ): ResponseEntity<Response> {
        val errorResponse = inventoryNotFoundException.message?.let {
            logger.info(it)
            Response(
                status = StatusResponses.ERROR,
                code = HttpStatus.NOT_FOUND,
                message = it,
                data = null
            )
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }
}