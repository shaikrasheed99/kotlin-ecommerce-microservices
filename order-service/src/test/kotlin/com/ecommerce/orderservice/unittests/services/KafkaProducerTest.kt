package com.ecommerce.orderservice.unittests.services

import com.ecommerce.orderservice.events.OrderPlacedEvent
import com.ecommerce.orderservice.services.KafkaProducer
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

class KafkaProducerTest : DescribeSpec({
    val mockKafkaTemplate = mockk<KafkaTemplate<String, OrderPlacedEvent>>()

    val kafkaProducer = KafkaProducer(mockKafkaTemplate)

    describe("Kafka Producer - annotations") {
        it("should have Service annotation to the Kafka Producer class") {
            val classAnnotations = kafkaProducer.javaClass.annotations
            val serviceAnnotation = classAnnotations.firstOrNull { it is Service } as Service

            serviceAnnotation shouldNotBe null
        }
    }

    describe("Send Order Placed Event") {
        it("should be able to send order placed event") {
            val orderPlacedEvent = createOrderPlacedEvent()

            val topicSlot = slot<String>()
            val eventSlot = slot<OrderPlacedEvent>()

            every {
                mockKafkaTemplate.send(capture(topicSlot), capture(eventSlot))
            } returns CompletableFuture<SendResult<String, OrderPlacedEvent>>()

            kafkaProducer.sendOrderPlacedEvent(TEST_KAFKA_TOPIC, orderPlacedEvent)

            topicSlot.captured shouldBe TEST_KAFKA_TOPIC
            eventSlot.captured shouldBe orderPlacedEvent

            verify {
                mockKafkaTemplate.send(TEST_KAFKA_TOPIC, orderPlacedEvent)
            }

            confirmVerified(mockKafkaTemplate)
        }
    }

    describe("Send Order Placed Event - Error scenarios") {
        it("should be able to throw exception when kafka templates throws exception") {
            val orderPlacedEvent = createOrderPlacedEvent()

            val exception = Exception("exception from kafka template")

            every {
                mockKafkaTemplate.send(TEST_KAFKA_TOPIC, orderPlacedEvent)
            } throws exception

            shouldThrow<Exception> {
                kafkaProducer.sendOrderPlacedEvent(TEST_KAFKA_TOPIC, orderPlacedEvent)
            }

            verify {
                mockKafkaTemplate.send(TEST_KAFKA_TOPIC, orderPlacedEvent)
            }

            confirmVerified(mockKafkaTemplate)
        }
    }
})

private fun createOrderPlacedEvent() = OrderPlacedEvent(
    id = UUID.randomUUID(),
    skuCode = "test skucode"
)