package com.ecommerce.productservice.exceptions

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerController {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        methodArgumentNotValidException: MethodArgumentNotValidException
    ): ResponseEntity<HashMap<String, String?>> {
        val fieldErrors = methodArgumentNotValidException.fieldErrors
        val errors = HashMap<String, String?>()

        fieldErrors.forEach {
            errors[it.field] = it.defaultMessage
        }

        return ResponseEntity.badRequest().body(errors)
    }
}