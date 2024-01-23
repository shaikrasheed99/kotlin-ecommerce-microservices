package com.ecommerce.productservice.unittests.controllers

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.controllers.HealthController
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus

class HealthControllerTest(): DescribeSpec({
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
})