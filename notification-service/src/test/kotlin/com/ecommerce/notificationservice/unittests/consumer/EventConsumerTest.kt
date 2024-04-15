package com.ecommerce.notificationservice.unittests.consumer

import com.ecommerce.notificationservice.constants.EventTypes
import com.ecommerce.notificationservice.consumer.EventConsumer
import com.ecommerce.notificationservice.models.Notification
import com.ecommerce.notificationservice.models.NotificationRepository
import com.ecommerce.notificationservice.utils.EmbeddedKafkaProducerTestUtils.createAndSerializeCloudEvent
import com.ecommerce.notificationservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.notificationservice.utils.TestUtils.createTestNotification
import com.ecommerce.notificationservice.utils.TestUtils.createTestOrderPlacedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private const val TEST_TOPIC = "testTopic"
private const val TEST_SOURCE = "test_source"

class EventConsumerTest : DescribeSpec({
    val mockNotificationRepository = mockk<NotificationRepository>()
    val eventConsumer = EventConsumer(mockNotificationRepository)

    val mapper = ObjectMapper()

    describe("Event Consumer - annotations") {
        it("should have Component annotation to the event consumer class") {
            val annotations = eventConsumer.javaClass.annotations
            val componentAnnotation = annotations.firstOrNull { it is Component } as Component

            componentAnnotation shouldNotBe null
        }
    }

    describe("consume") {
        it("should be able to save order placed events to notifications table") {
            val orderPlacedEventJson = mapper.writeValueAsString(createTestOrderPlacedEvent())
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            val notification = createTestNotification()
            every { mockNotificationRepository.save(any(Notification::class)) } returns notification

            eventConsumer.consume(payload, TEST_TOPIC)

            verify { mockNotificationRepository.save(any(Notification::class)) }
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

        it("should be able to throw Exception when repository layer throws any exception") {
            val orderPlacedEventJson = mapper.writeValueAsString(createTestOrderPlacedEvent())
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            val exception = Exception("exception from notification repository layer")
            every { mockNotificationRepository.save(any(Notification::class)) } throws exception

            val thrownException = shouldThrow<Exception> {
                eventConsumer.consume(payload, TEST_TOPIC)
            }

            thrownException.message shouldBe exception.message

            verify { mockNotificationRepository.save(any(Notification::class)) }
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
