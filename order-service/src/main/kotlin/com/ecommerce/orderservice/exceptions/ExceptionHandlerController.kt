package com.ecommerce.orderservice.exceptions

import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.responses.SuccessResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerController {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        methodArgumentNotValidException: MethodArgumentNotValidException
    ): ResponseEntity<HashMap<String, String?>> {
        val errors = HashMap<String, String?>()
        val fieldErrors = methodArgumentNotValidException.fieldErrors

        fieldErrors.forEach {
            logger.info(it.field + it.defaultMessage)
            errors[it.field] = it.defaultMessage
        }

        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(InventoryNotFoundException::class)
    fun handleInventoryNotFoundException(
        inventoryNotFoundException: InventoryNotFoundException
    ): ResponseEntity<SuccessResponse> {
        val response = inventoryNotFoundException.message?.let {
            logger.info(it)
            SuccessResponse(
                status = StatusResponses.ERROR,
                code = HttpStatus.NOT_FOUND,
                message = it,
                data = null
            )
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }
}