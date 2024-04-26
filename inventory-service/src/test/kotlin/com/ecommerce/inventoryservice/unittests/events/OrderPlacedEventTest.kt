package com.ecommerce.inventoryservice.unittests.events

import com.ecommerce.inventoryservice.utils.TestUtils.createTestOrderPlacedEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class OrderPlacedEventTest : DescribeSpec({
    describe("OrderPlacedEvent - getters") {
        it("should have all the attributes in the response") {
            val orderPlacedEvent = createTestOrderPlacedEvent()

            orderPlacedEvent.orderId shouldBe UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10")
            orderPlacedEvent.skuCode shouldBe "test_skuCode"
            orderPlacedEvent.quantity shouldBe 10
        }
    }

    describe("OrderPlacedEvent - setters") {
        it("should be able to change Id attribute of the order placed event") {
            val orderPlacedEvent = createTestOrderPlacedEvent()
            orderPlacedEvent.orderId = UUID.fromString("bbb8a937-0504-4468-823a-04ccd6964d10")

            orderPlacedEvent.orderId shouldBe UUID.fromString("bbb8a937-0504-4468-823a-04ccd6964d10")
        }

        it("should be able to change SkuCode attribute of the order placed event") {
            val orderPlacedEvent = createTestOrderPlacedEvent()
            orderPlacedEvent.skuCode = "another skuCode"

            orderPlacedEvent.skuCode shouldBe "another skuCode"
        }

        it("should be able to change Quantity attribute of the order placed event") {
            val orderPlacedEvent = createTestOrderPlacedEvent()
            orderPlacedEvent.quantity = 20

            orderPlacedEvent.quantity shouldBe 20
        }
    }
})
