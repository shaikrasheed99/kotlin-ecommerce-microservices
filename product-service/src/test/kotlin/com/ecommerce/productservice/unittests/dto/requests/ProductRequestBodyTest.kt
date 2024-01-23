package com.ecommerce.productservice.unittests.dto.requests

import com.ecommerce.productservice.utils.TestUtils.createProductRequestBody
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class ProductRequestBodyTest: DescribeSpec({
    describe("Product Request Body - getters") {
        it("should be able to have all the attributes in the request body") {
            val productRequestBody = createProductRequestBody()

            productRequestBody.name shouldBe "test name"
            productRequestBody.description shouldBe "test description"
            productRequestBody.price shouldBe BigDecimal(10.0)
        }
    }

    describe("Product Request Body - setters") {
        it("should be able to change name attribute of the request body") {
            val productRequestBody = createProductRequestBody()
            productRequestBody.name = "another name"

            productRequestBody.name shouldBe "another name"
        }

        it("should be able to change description attribute of the request body") {
            val productRequestBody = createProductRequestBody()
            productRequestBody.description = "another description"

            productRequestBody.description shouldBe "another description"
        }

        it("should be able to change price attribute of the request body") {
            val productRequestBody = createProductRequestBody()
            productRequestBody.price = BigDecimal(20)

            productRequestBody.price shouldBe BigDecimal(20)
        }
    }
})