package com.ecommerce.productservice.unittests.exceptions

import com.ecommerce.productservice.exceptions.ExceptionHandler
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.web.bind.annotation.ControllerAdvice

class ExceptionHandlerTest : DescribeSpec({
    val exceptionHandler = ExceptionHandler()

    describe("Exception Handler - annotations") {
        it("should have ControllerAdvice annotation to the Exception handler") {
            val controllerAdviceAnnotation =
                exceptionHandler.javaClass.annotations.firstOrNull { it is ControllerAdvice } as ControllerAdvice

            controllerAdviceAnnotation shouldNotBe null
        }
    }
})