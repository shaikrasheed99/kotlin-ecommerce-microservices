package com.ecommerce.productservice.unittests.controllers

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.controllers.HealthController
import com.ecommerce.productservice.utils.EntityUtils.getMethodAnnotations
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

class HealthControllerTest : DescribeSpec({
    val healthController = HealthController()

    describe("Health Controller") {
        it("should be able to return Success Response when Server is up") {
            val responseBody = healthController.health().body

            responseBody?.status shouldBe StatusResponses.SUCCESS
            responseBody?.code shouldBe HttpStatus.OK
            responseBody?.message shouldBe MessageResponses.SERVER_UP.message
            responseBody?.data shouldBe null
        }
    }

    describe("Health Controller - annotations") {
        it("should have RestController annotation to the Health Controller class") {
            val classAnnotations = healthController.javaClass.annotations
            val restControllerAnnotation = classAnnotations.firstOrNull { it is RestController } as RestController

            restControllerAnnotation shouldNotBe null
        }

        it("should have GetMapping annotation to the health method") {
            val annotations = healthController.getMethodAnnotations("health")
            val getMappingAnnotation = annotations.firstOrNull { it is GetMapping } as GetMapping

            getMappingAnnotation shouldNotBe null
            getMappingAnnotation.value.firstOrNull { it == "/health" } shouldNotBe null
        }
    }
})
