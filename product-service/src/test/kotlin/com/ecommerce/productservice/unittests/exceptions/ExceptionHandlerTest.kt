package com.ecommerce.productservice.unittests.exceptions

import com.ecommerce.productservice.exceptions.ExceptionsHandler
import com.ecommerce.productservice.utils.EntityUtils.getMethodAnnotations
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class ExceptionHandlerTest : DescribeSpec({
    val exceptionsHandler = ExceptionsHandler()

    describe("Exception Handler - annotations") {
        it("should have ControllerAdvice annotation to the Exception handler") {
            val classAnnotations = exceptionsHandler.javaClass.annotations
            val controllerAdviceAnnotation = classAnnotations.firstOrNull { it is ControllerAdvice } as ControllerAdvice

            controllerAdviceAnnotation shouldNotBe null
        }
    }

    describe("handleMethodArgumentNotValidException") {
        it("should handle MethodArgumentNotValidException and return Bad Request status code") {
            val mockException = mockk<MethodArgumentNotValidException>()
            val fieldError = FieldError(
                "test object name",
                "test_field_name",
                "test default message"
            )
            every { mockException.fieldErrors } returns mutableListOf(fieldError)
            val errors = mapOf(fieldError.field to fieldError.defaultMessage)

            val response = exceptionsHandler.handleMethodArgumentNotValidException(mockException)

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
            response.body shouldBe errors
        }
    }

    describe("handleMethodArgumentNotValidException - annotations") {
        it("should have ExceptionHandler annotation with MethodArgumentNotValidException class as value") {
            val annotations = exceptionsHandler.getMethodAnnotations("handleMethodArgumentNotValidException")
            val exceptionHandlerAnnotation = annotations.firstOrNull { it is ExceptionHandler } as ExceptionHandler

            exceptionHandlerAnnotation shouldNotBe null
            exceptionHandlerAnnotation.value.firstOrNull {
                it == MethodArgumentNotValidException::class
            } shouldNotBe null
        }
    }
})