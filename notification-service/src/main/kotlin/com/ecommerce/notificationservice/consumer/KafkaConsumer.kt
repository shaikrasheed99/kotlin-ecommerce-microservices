package com.ecommerce.notificationservice.consumer

import com.ecommerce.notificationservice.events.OrderPlacedEvent
import com.ecommerce.notificationservice.models.Notification
import com.ecommerce.notificationservice.models.NotificationRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.Instant

@Component
class KafkaConsumer(private val notificationRepository: NotificationRepository) {
    @KafkaListener(topics = ["notificationsTopic"])
    fun handleOrderPlacedEvent(orderPlacedEvent: OrderPlacedEvent) {
        logger.info(
            "Received event with order id: ${orderPlacedEvent.id} and skucode: ${orderPlacedEvent.skuCode}"
        )

        val notification = mapToNotification(orderPlacedEvent)
        notificationRepository.save(notification)
    }

    private fun mapToNotification(orderPlacedEvent: OrderPlacedEvent): Notification =
        Notification(
            id = null,
            sender = "ecommerce.microservices@gmail.com",
            recipient = "test@gmail.com",
            isSent = false,
            orderId = orderPlacedEvent.id,
            skuCode = orderPlacedEvent.skuCode,
            createdAt = Timestamp.from(Instant.now())
        )

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
