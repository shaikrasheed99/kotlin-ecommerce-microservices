package com.ecommerce.productservice.exceptions

import com.ecommerce.productservice.dto.response.ErrorResponse
import com.ecommerce.productservice.utils.createErrorResponse
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
        val fieldErrors = methodArgumentNotValidException.fieldErrors
        val errors = HashMap<String, String?>()

        fieldErrors.forEach {
            logger.info(it.field + it.defaultMessage)
            errors[it.field] = it.defaultMessage
        }

        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(ProductNotFound::class)
    fun handleProductNotFound(
        productNotFound: ProductNotFound
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = productNotFound.message?.let {
            logger.info(it)
            createErrorResponse(HttpStatus.NOT_FOUND, it)
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }
}