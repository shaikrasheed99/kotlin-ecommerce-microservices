package com.ecommerce.orderservice.exceptions

import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.responses.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionsHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        methodArgumentNotValidException: MethodArgumentNotValidException
    ): ResponseEntity<HashMap<String, String?>> {
        val errors = HashMap<String, String?>()
        val fieldErrors = methodArgumentNotValidException.fieldErrors

        fieldErrors.forEach {
            logger.info(it.defaultMessage)
            errors[it.field] = it.defaultMessage
        }

        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(
        value = [
            InsufficientInventoryQuantityException::class,
            InventoryServiceErrorException::class
        ]
    )
    fun handleInventoryServiceExceptions(
        exception: Exception
    ): ResponseEntity<Response> {
        val response = exception.message!!.let {
            logger.info(it)
            Response(
                status = StatusResponses.ERROR,
                code = HttpStatus.INTERNAL_SERVER_ERROR,
                message = it,
                data = null
            )
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
