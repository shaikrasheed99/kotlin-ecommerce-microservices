package com.ecommerce.orderservice.exceptions

import org.slf4j.LoggerFactory
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
}