package com.ecommerce.notificationservice.unittests.consumer

import com.ecommerce.notificationservice.consumer.KafkaConsumer
import com.ecommerce.notificationservice.events.OrderPlacedEvent
import com.ecommerce.notificationservice.models.Notification
import com.ecommerce.notificationservice.models.NotificationRepository
import com.ecommerce.notificationservice.utils.EntityUtils.getMethodAnnotations
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

class KafkaConsumerTest : DescribeSpec({
    val mockNotificationRepository = mockk<NotificationRepository>()
    val kafkaConsumer = KafkaConsumer(mockNotificationRepository)

    describe("Kafka Consumer - annotations") {
        it("should have Component annotation to the kafka consumer class") {
            val annotations = kafkaConsumer.javaClass.annotations
            val componentAnnotation = annotations.firstOrNull { it is Component } as Component

            componentAnnotation shouldNotBe null
        }
    }

    describe("handleOrderPlacedEvent") {
        it("should be able to save order placed events to notifications table") {
            val orderPlacedEvent = createTestOrderPlacedEvent()
            val notification = createTestNotification()
            every { mockNotificationRepository.save(any(Notification::class)) } returns notification

            kafkaConsumer.handleOrderPlacedEvent(orderPlacedEvent)

            verify { mockNotificationRepository.save(any(Notification::class)) }
        }
    }

    describe("handleOrderPlacedEvent - annotations") {
        it("should have KafkaListener annotation to the handleOrderPlacedEvent method") {
            val methodAnnotations = kafkaConsumer.getMethodAnnotations("handleOrderPlacedEvent")
            val kafkaListenerAnnotation = methodAnnotations.firstOrNull { it is KafkaListener } as KafkaListener

            kafkaListenerAnnotation shouldNotBe null
            kafkaListenerAnnotation.topics.first() shouldBe "notificationsTopic"
        }
    }

    describe("Kafka Consumer - logger") {
        it("should initialize the logger of kafka consumer") {
            KafkaConsumer.logger shouldNotBe null
        }
    }
})

private fun createTestNotification() = Notification(
    id = UUID.randomUUID(),
    sender = "test@gmail.com",
    recipient = "test@gmail.com",
    isSent = false,
    orderId = UUID.randomUUID(),
    skuCode = "test_skucode",
    createdAt = Timestamp.from(Instant.now())
)

private fun createTestOrderPlacedEvent(): OrderPlacedEvent =
    OrderPlacedEvent(
        id = UUID.randomUUID(),
        skuCode = "test_skucode"
    )
