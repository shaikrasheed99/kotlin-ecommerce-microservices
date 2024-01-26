package com.ecommerce.inventoryservice.unittests.exceptions

import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.dto.responses.Response
import com.ecommerce.inventoryservice.exceptions.ExceptionsHandler
import com.ecommerce.inventoryservice.exceptions.InventoryNotFoundException
import com.ecommerce.inventoryservice.utils.EntityUtils.getMethodAnnotations
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class ExceptionsHandlerTest : DescribeSpec({
    val exceptionsHandler = ExceptionsHandler()

    describe("Exceptions Handler - annotations") {
        it("should have ControllerAdvice annotation to the Exception handler") {
            val classAnnotations = exceptionsHandler.javaClass.annotations
            val controllerAdviceAnnotation = classAnnotations.firstOrNull { it is ControllerAdvice } as ControllerAdvice

            controllerAdviceAnnotation shouldNotBe null
        }
    }

    describe("handleInventoryNotFoundException") {
        it("should handle InventoryNotFoundException and return Not Found status code") {
            val mockException = mockk<InventoryNotFoundException>()
            val errorResponse = Response(
                status = StatusResponses.ERROR,
                code = HttpStatus.NOT_FOUND,
                message = "inventory not found",
                data = null
            )
            every { mockException.message } returns errorResponse.message

            val response = exceptionsHandler.handleInventoryNotFoundException(mockException)

            response.statusCode shouldBe HttpStatus.NOT_FOUND
            response.body shouldBe errorResponse

            verify { mockException.message }
        }
    }

    describe("handleInventoryNotFoundException - annotations") {
        it("should have ExceptionHandler annotation with InventoryNotFoundException class as value") {
            val annotations = exceptionsHandler.getMethodAnnotations("handleInventoryNotFoundException")
            val exceptionHandlerAnnotation = annotations.firstOrNull { it is ExceptionHandler } as ExceptionHandler

            exceptionHandlerAnnotation shouldNotBe null
            exceptionHandlerAnnotation.value.firstOrNull {
                it == InventoryNotFoundException::class
            } shouldBe InventoryNotFoundException::class
        }
    }

    describe("Exceptions Handler - logger") {
        it("should initialize the logger of exceptions handler") {
            ExceptionsHandler.logger shouldNotBe null
        }
    }
})