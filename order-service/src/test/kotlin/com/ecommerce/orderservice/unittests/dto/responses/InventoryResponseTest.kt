package com.ecommerce.orderservice.unittests.dto.responses

import com.ecommerce.orderservice.dto.responses.InventoryResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class InventoryResponseTest : DescribeSpec({
    describe("Inventory Response - getters") {
        it("should be able to have all the attributes in the inventory response") {
            val response = createTestInventoryResponse()

            response.id shouldBe 1
            response.skuCode shouldBe "test_code"
            response.quantity shouldBe 10
        }
    }

    describe("Inventory Response - setters") {
        it("should be able to change Id attribute of the inventory response") {
            val response = createTestInventoryResponse()
            response.id = 10

            response.id shouldBe 10
        }

        it("should be able to change SkuCode attribute of the inventory response") {
            val response = createTestInventoryResponse()
            response.skuCode = "another skuCode"

            response.skuCode shouldBe "another skuCode"
        }

        it("should be able to change Quantity attribute of the inventory response") {
            val response = createTestInventoryResponse()
            response.quantity = 20

            response.quantity shouldBe 20
        }
    }
})

fun createTestInventoryResponse(): InventoryResponse = InventoryResponse(
    id = 1,
    skuCode = "test_code",
    quantity = 10
)
