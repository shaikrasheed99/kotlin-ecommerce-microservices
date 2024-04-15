package com.ecommerce.notificationservice.unittests.consumer

import com.ecommerce.notificationservice.consumer.EventConsumer
import com.ecommerce.notificationservice.models.Notification
import com.ecommerce.notificationservice.models.NotificationRepository
import com.ecommerce.notificationservice.utils.EmbeddedKafkaProducerTestUtils.createAndSerializeCloudEvent
import com.ecommerce.notificationservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.notificationservice.utils.TestUtils.createTestNotification
import com.ecommerce.notificationservice.utils.TestUtils.createTestOrderPlacedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private const val TEST_TOPIC = "testTopic"
private const val ORDER_PLACED_EVENT_TYPE = "order_placed"
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
                eventType = ORDER_PLACED_EVENT_TYPE,
                source = TEST_SOURCE
            )
            val notification = createTestNotification()
            every { mockNotificationRepository.save(any(Notification::class)) } returns notification

            eventConsumer.consume(payload, TEST_TOPIC)

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
