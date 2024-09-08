package com.ecommerce.notificationservice.services

import com.ecommerce.notificationservice.events.OrderPlacedEvent
import com.ecommerce.notificationservice.models.notification.Notification
import com.ecommerce.notificationservice.models.notification.NotificationRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class NotificationService(private val notificationRepository: NotificationRepository) {
    fun getRecentNotification(): List<Notification> {
        return notificationRepository.findFirstByOrderByCreatedAtDesc()
    }

    fun saveNotificationWith(orderPlacedEvent: OrderPlacedEvent) {
        val notification = mapToNotification(orderPlacedEvent)
        notificationRepository.save(notification)
    }

    private fun mapToNotification(orderPlacedEvent: OrderPlacedEvent): Notification =
        Notification(
            id = null,
            sender = "ecommerce.microservices@gmail.com",
            recipient = "test@gmail.com",
            isSent = false,
            orderId = orderPlacedEvent.orderId,
            skuCode = orderPlacedEvent.skuCode,
            createdAt = Timestamp.from(Instant.now())
        )
}
