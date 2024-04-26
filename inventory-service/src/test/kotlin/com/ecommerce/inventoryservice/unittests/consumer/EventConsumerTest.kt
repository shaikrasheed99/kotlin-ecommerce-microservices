package com.ecommerce.inventoryservice.unittests.consumer

import com.ecommerce.inventoryservice.constants.EventTypes
import com.ecommerce.inventoryservice.consumer.EventConsumer
import com.ecommerce.inventoryservice.services.InventoryService
import com.ecommerce.inventoryservice.utils.EmbeddedKafkaProducerTestUtils.createAndSerializeCloudEvent
import com.ecommerce.inventoryservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.inventoryservice.utils.TestUtils.createTestOrderPlacedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private const val TEST_TOPIC = "testTopic"
private const val TEST_SOURCE = "test_source"

class EventConsumerTest : DescribeSpec({
    val mockInventoryService = mockk<InventoryService>()
    val eventConsumer = EventConsumer(mockInventoryService)

    val mapper = ObjectMapper()

    describe("Event Consumer - annotations") {
        it("should have Component annotation to the event consumer class") {
            val annotations = eventConsumer.javaClass.annotations
            val componentAnnotation = annotations.firstOrNull { it is Component } as Component

            componentAnnotation shouldNotBe null
        }
    }

    describe("consume") {
        it("should be able to update quantity of inventory by the skucode from the data order placed event") {
            val orderPlacedEvent = createTestOrderPlacedEvent()
            val orderPlacedEventJson = mapper.writeValueAsString(orderPlacedEvent)
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            every {
                mockInventoryService.updateInventoryQuantityBySkucode(
                    orderPlacedEvent.skuCode,
                    orderPlacedEvent.quantity
                )
            } just runs

            eventConsumer.consume(payload, TEST_TOPIC)

            verify {
                mockInventoryService.updateInventoryQuantityBySkucode(
                    orderPlacedEvent.skuCode,
                    orderPlacedEvent.quantity
                )
            }
        }

        it("should be able to skip events which are not order placed events") {
            val orderPlacedEventJson = mapper.writeValueAsString(createTestOrderPlacedEvent())
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = "testEventType",
                source = TEST_SOURCE
            )

            shouldNotThrowAny {
                eventConsumer.consume(payload, TEST_TOPIC)
            }
        }
    }

    describe("consume - Error scenarios") {
        it("should be able to throw MismatchedInputException when invalid order placed event is passed") {
            val invalidOrderPlacedEventJson = mapper.writeValueAsString("invalid order placed event")
            val payload = createAndSerializeCloudEvent(
                event = invalidOrderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )

            shouldThrow<MismatchedInputException> {
                eventConsumer.consume(payload, TEST_TOPIC)
            }
        }

        it("should be able to throw Exception when service layer throws any exception") {
            val orderPlacedEvent = createTestOrderPlacedEvent()
            val orderPlacedEventJson = mapper.writeValueAsString(orderPlacedEvent)
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            val exception = Exception("exception from inventory repository layer")
            every {
                mockInventoryService.updateInventoryQuantityBySkucode(
                    orderPlacedEvent.skuCode,
                    orderPlacedEvent.quantity
                )
            } throws exception

            val thrownException = shouldThrow<Exception> {
                eventConsumer.consume(payload, TEST_TOPIC)
            }

            thrownException.message shouldBe exception.message

            verify {
                mockInventoryService.updateInventoryQuantityBySkucode(
                    orderPlacedEvent.skuCode,
                    orderPlacedEvent.quantity
                )
            }
        }
    }

    describe("consume - annotations") {
        it("should have KafkaListener annotation to the consume method") {
            val methodAnnotations = eventConsumer.getMethodAnnotations("consume")
            val kafkaListenerAnnotation = methodAnnotations.firstOrNull { it is KafkaListener } as KafkaListener

            kafkaListenerAnnotation shouldNotBe null
            kafkaListenerAnnotation.topics.first() shouldBe "\${spring.kafka.topic}"
        }
    }

    describe("Event Consumer - logger") {
        it("should initialize the logger of event consumer") {
            EventConsumer.logger shouldNotBe null
        }
    }
})
