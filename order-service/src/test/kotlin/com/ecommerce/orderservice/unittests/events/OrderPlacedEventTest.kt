package com.ecommerce.orderservice.unittests.events

import com.ecommerce.orderservice.events.OrderPlacedEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class OrderPlacedEventTest : DescribeSpec({
    describe("OrderPlacedEvent - getters") {
        it("should be able to have all the attributes in the response") {
            val orderPlacedEvent = createTestOrderPlacedEvent()

            orderPlacedEvent.orderId shouldBe UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10")
            orderPlacedEvent.skuCode shouldBe "test_skuCode"
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
    }
})

fun createTestOrderPlacedEvent(): OrderPlacedEvent = OrderPlacedEvent(
    orderId = UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10"),
    skuCode = "test_skuCode"
)
