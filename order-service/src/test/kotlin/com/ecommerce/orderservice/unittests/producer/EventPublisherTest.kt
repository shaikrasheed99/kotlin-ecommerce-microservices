package com.ecommerce.orderservice.unittests.producer

import com.ecommerce.orderservice.events.OrderPlacedEvent
import com.ecommerce.orderservice.producer.EventPublisher
import com.ecommerce.orderservice.utils.TestUtils.convertEventToJsonString
import com.ecommerce.orderservice.utils.TestUtils.createOutbox
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.CompletableFuture

private const val TEST_KAFKA_TOPIC = "testTopic"
const val TEST_ORDER_PLACED_EVENT_TYPE = "test_order_placed"

class EventPublisherTest : DescribeSpec({
    val mockKafkaTemplate = mockk<KafkaTemplate<String, String>>()

    val eventPublisher = EventPublisher(mockKafkaTemplate)

    describe("Kafka Producer - annotations") {
        it("should have Service annotation to the Kafka Producer class") {
            val classAnnotations = eventPublisher.javaClass.annotations
            val serviceAnnotation = classAnnotations.firstOrNull { it is Service } as Service

            serviceAnnotation shouldNotBe null
        }
    }

    describe("Send Order Placed Event") {
        it("should be able to send order placed event") {
            val orderPlacedEvent = createOrderPlacedEvent()
            val eventId = UUID.randomUUID()
            val outbox = createOutbox(
                id = eventId,
                eventType = TEST_ORDER_PLACED_EVENT_TYPE,
                eventPayload = convertEventToJsonString(orderPlacedEvent),
                topic = TEST_KAFKA_TOPIC
            )

            val topicSlot = slot<String>()
            val messageSlot = slot<String>()

            every {
                mockKafkaTemplate.send(capture(topicSlot), capture(messageSlot))
            } returns CompletableFuture<SendResult<String, String>>()

            eventPublisher.publish(outbox)

            topicSlot.captured shouldBe TEST_KAFKA_TOPIC
            messageSlot.captured.shouldContainJsonKeyValue("$.id", eventId.toString())
            messageSlot.captured.shouldContainJsonKeyValue("$.type", TEST_ORDER_PLACED_EVENT_TYPE)
            messageSlot.captured.shouldContainJsonKeyValue("$.data.orderId", orderPlacedEvent.orderId.toString())
            messageSlot.captured.shouldContainJsonKeyValue("$.data.skuCode", orderPlacedEvent.skuCode)
            messageSlot.captured.shouldContainJsonKeyValue("$.data.quantity", orderPlacedEvent.quantity)

            verify {
                mockKafkaTemplate.send(TEST_KAFKA_TOPIC, any(String::class))
            }

            confirmVerified(mockKafkaTemplate)
        }
    }

    describe("Send Order Placed Event - Error scenarios") {
        it("should be able to throw exception when kafka templates throws exception") {
            val orderPlacedEvent = createOrderPlacedEvent()
            val eventId = UUID.randomUUID()
            val outbox = createOutbox(
                id = eventId,
                eventType = TEST_ORDER_PLACED_EVENT_TYPE,
                eventPayload = convertEventToJsonString(orderPlacedEvent),
                topic = TEST_KAFKA_TOPIC
            )

            val exception = Exception("exception from kafka template")

            every {
                mockKafkaTemplate.send(TEST_KAFKA_TOPIC, any(String::class))
            } throws exception

            shouldThrow<Exception> {
                eventPublisher.publish(outbox)
            }

            verify {
                mockKafkaTemplate.send(TEST_KAFKA_TOPIC, any(String::class))
            }

            confirmVerified(mockKafkaTemplate)
        }
    }

    describe("Kafka Producer - logger") {
        it("should initialize the logger of kafka producer") {
            EventPublisher.logger shouldNotBe null
        }
    }
})

private fun createOrderPlacedEvent() = OrderPlacedEvent(
    orderId = UUID.randomUUID(),
    skuCode = "test skucode",
    quantity = 10
)
