package com.ecommerce.notificationservice.unittests.consumer

import com.ecommerce.notificationservice.consumer.EventConsumer
import com.ecommerce.notificationservice.models.Notification
import com.ecommerce.notificationservice.models.NotificationRepository
import com.ecommerce.notificationservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.notificationservice.utils.TestUtils.createTestNotification
import com.ecommerce.notificationservice.utils.TestUtils.createTestOrderPlacedEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

class EventConsumerTest : DescribeSpec({
    val mockNotificationRepository = mockk<NotificationRepository>()
    val eventConsumer = EventConsumer(mockNotificationRepository)

    describe("Kafka Consumer - annotations") {
        it("should have Component annotation to the event consumer class") {
            val annotations = eventConsumer.javaClass.annotations
            val componentAnnotation = annotations.firstOrNull { it is Component } as Component

            componentAnnotation shouldNotBe null
        }
    }

    describe("handleOrderPlacedEvent") {
        it("should be able to save order placed events to notifications table") {
            val orderPlacedEvent = createTestOrderPlacedEvent()
            val notification = createTestNotification()
            every { mockNotificationRepository.save(any(Notification::class)) } returns notification

            eventConsumer.handleOrderPlacedEvent(orderPlacedEvent)

            verify { mockNotificationRepository.save(any(Notification::class)) }
        }
    }

    describe("handleOrderPlacedEvent - annotations") {
        it("should have KafkaListener annotation to the handleOrderPlacedEvent method") {
            val methodAnnotations = eventConsumer.getMethodAnnotations("handleOrderPlacedEvent")
            val kafkaListenerAnnotation = methodAnnotations.firstOrNull { it is KafkaListener } as KafkaListener

            kafkaListenerAnnotation shouldNotBe null
            kafkaListenerAnnotation.topics.first() shouldBe "\${spring.kafka.topic}"
        }
    }

    describe("Kafka Consumer - logger") {
        it("should initialize the logger of event consumer") {
            EventConsumer.logger shouldNotBe null
        }
    }
})
