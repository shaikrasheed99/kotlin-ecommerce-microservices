package com.ecommerce.notificationservice.utils

import com.ecommerce.notificationservice.events.OrderPlacedEvent
import com.ecommerce.notificationservice.models.Notification
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

object TestUtils {
    fun createTestNotification() = Notification(
        id = UUID.randomUUID(),
        sender = "test@gmail.com",
        recipient = "test@gmail.com",
        isSent = false,
        orderId = UUID.randomUUID(),
        skuCode = "test_skucode",
        createdAt = Timestamp.from(Instant.now())
    )

    fun createTestOrderPlacedEvent(): OrderPlacedEvent = OrderPlacedEvent(
        id = UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10"),
        skuCode = "test_skuCode"
    )
}