package com.ecommerce.orderservice.unittests.dto.responses

import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.responses.Response
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus

class ResponseTest : DescribeSpec({
    describe("Response - getters") {
        it("should be able to have all the attributes in the response") {
            val response = createTestResponse()

            response.status shouldBe StatusResponses.SUCCESS
            response.code shouldBe HttpStatus.OK
            response.message shouldBe "test message"
            response.data shouldBe "test data"
        }
    }

    describe("Response - setters") {
        it("should be able to change status attribute of the response") {
            val response = createTestResponse()
            response.status = StatusResponses.ERROR

            response.status shouldBe StatusResponses.ERROR
        }

        it("should be able to change code attribute of the response") {
            val response = createTestResponse()
            response.code = HttpStatus.NOT_FOUND

            response.code shouldBe HttpStatus.NOT_FOUND
        }

        it("should be able to change message attribute of the response") {
            val response = createTestResponse()
            response.message = "another message"

            response.message shouldBe "another message"
        }

        it("should be able to change data attribute of the response") {
            val response = createTestResponse()
            response.data = "another data"

            response.data shouldBe "another data"
        }
    }
})

fun createTestResponse(): Response = Response(
    status = StatusResponses.SUCCESS,
    code = HttpStatus.OK,
    message = "test message",
    data = "test data"
)
