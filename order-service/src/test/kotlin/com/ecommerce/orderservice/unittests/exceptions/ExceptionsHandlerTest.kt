package com.ecommerce.orderservice.unittests.exceptions

import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.responses.Response
import com.ecommerce.orderservice.exceptions.ExceptionsHandler
import com.ecommerce.orderservice.exceptions.InsufficientInventoryQuantityException
import com.ecommerce.orderservice.exceptions.InventoryNotAvailableException
import com.ecommerce.orderservice.exceptions.InventoryServiceErrorException
import com.ecommerce.orderservice.utils.EntityUtils.getMethodAnnotations
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
        it("should have ControllerAdvice annotation to the Exceptions handler") {
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

    describe("handleInventoryServiceExceptions") {
        it("should handle InventoryServiceExceptions and return Internal Server Error status code") {
            val mockInsufficientInventoryQuantityException = mockk<InsufficientInventoryQuantityException>()
            val errorResponse = Response(
                status = StatusResponses.ERROR,
                code = HttpStatus.INTERNAL_SERVER_ERROR,
                message = "Insufficient Inventory Quantity",
                data = null
            )
            every { mockInsufficientInventoryQuantityException.message } returns errorResponse.message

            val response =
                exceptionsHandler.handleInventoryServiceExceptions(mockInsufficientInventoryQuantityException)

            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
            response.body shouldBe errorResponse

            verify { mockInsufficientInventoryQuantityException.message }
        }
    }

    describe("handleInventoryServiceExceptions - annotations") {
        it("should have ExceptionHandler annotation with relevant classes as value") {
            val annotations = exceptionsHandler.getMethodAnnotations("handleInventoryServiceExceptions")
            val exceptionHandlerAnnotation = annotations.firstOrNull { it is ExceptionHandler } as ExceptionHandler

            exceptionHandlerAnnotation shouldNotBe null
            exceptionHandlerAnnotation.value shouldBe mutableListOf(
                InsufficientInventoryQuantityException::class,
                InventoryNotAvailableException::class,
                InventoryServiceErrorException::class
            )
        }
    }

    describe("Exception Handler - logger") {
        it("should initialize the logger of exception handler") {
            ExceptionsHandler.logger shouldNotBe null
        }
    }
})
