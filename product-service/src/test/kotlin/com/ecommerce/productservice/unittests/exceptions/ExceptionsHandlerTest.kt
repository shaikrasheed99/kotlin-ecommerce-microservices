package com.ecommerce.productservice.unittests.exceptions

import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.dto.response.Response
import com.ecommerce.productservice.exceptions.ExceptionsHandler
import com.ecommerce.productservice.exceptions.ProductNotFound
import com.ecommerce.productservice.utils.EntityUtils.getMethodAnnotations
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class ExceptionsHandlerTest : DescribeSpec({
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

            verify { mockException.fieldErrors }
        }
    }

    describe("handleMethodArgumentNotValidException - annotations") {
        it("should have ExceptionHandler annotation with MethodArgumentNotValidException class as value") {
            val annotations = exceptionsHandler.getMethodAnnotations("handleMethodArgumentNotValidException")
            val exceptionHandlerAnnotation = annotations.firstOrNull { it is ExceptionHandler } as ExceptionHandler

            exceptionHandlerAnnotation shouldNotBe null
            exceptionHandlerAnnotation.value.firstOrNull {
                it == MethodArgumentNotValidException::class
            } shouldBe MethodArgumentNotValidException::class
        }
    }

    describe("handleProductNotFound") {
        it("should handle ProductNotFound and return Not Found status code") {
            val mockException = mockk<ProductNotFound>()
            val errorResponse = Response(
                status = StatusResponses.ERROR,
                code = HttpStatus.NOT_FOUND,
                message = "product not found",
                data = null
            )
            every { mockException.message } returns errorResponse.message

            val response = exceptionsHandler.handleProductNotFound(mockException)

            response.statusCode shouldBe HttpStatus.NOT_FOUND
            response.body shouldBe errorResponse

            verify { mockException.message }
        }
    }

    describe("handleProductNotFound - annotations") {
        it("should have ExceptionHandler annotation with ProductNotFound class as value") {
            val annotations = exceptionsHandler.getMethodAnnotations("handleProductNotFound")
            val exceptionHandlerAnnotation = annotations.firstOrNull { it is ExceptionHandler } as ExceptionHandler

            exceptionHandlerAnnotation shouldNotBe null
            exceptionHandlerAnnotation.value.firstOrNull {
                it == ProductNotFound::class
            } shouldBe ProductNotFound::class
        }
    }

    describe("Exception Handler - logger") {
        it("should initialize the logger of exception handler") {
            ExceptionsHandler.logger shouldNotBe null
        }
    }
})
