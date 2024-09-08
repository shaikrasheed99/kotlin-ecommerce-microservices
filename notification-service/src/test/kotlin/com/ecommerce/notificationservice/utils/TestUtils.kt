package com.ecommerce.notificationservice.utils

import com.ecommerce.notificationservice.constants.StatusResponses
import com.ecommerce.notificationservice.events.OrderPlacedEvent
import com.ecommerce.notificationservice.models.inbox.Inbox
import com.ecommerce.notificationservice.models.notification.Notification
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

object TestUtils {
    fun createTestNotification(
        skuCode: String = "test_skucode",
        createdAt: Timestamp = Timestamp.from(Instant.now())
    ) = Notification(
        id = UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10"),
        sender = "test@gmail.com",
        recipient = "test@gmail.com",
        isSent = false,
        orderId = UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10"),
        skuCode = skuCode,
        createdAt = createdAt
    )

    fun createTestOrderPlacedEvent(): OrderPlacedEvent = OrderPlacedEvent(
        orderId = UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10"),
        skuCode = "test_skuCode"
    )

    fun createInbox(
        id: UUID = UUID.randomUUID(),
        eventId: UUID = UUID.randomUUID(),
        eventType: String = "test_type",
        topic: String = "test_topic"
    ) = Inbox(
        id = id,
        eventId = eventId,
        eventType = eventType,
        topic = topic
    )

    fun getPostgreSQLContainerWithTableScript(script: String): PostgreSQLContainer<*>? =
        PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("notifications")
            .withInitScript(script)

    fun MockMvcResultMatchersDsl.assertCommonResponseBody(
        status: StatusResponses,
        code: HttpStatus,
        message: String
    ) {
        jsonPath("$.status") { value(status.name) }
        jsonPath("$.code") { value(code.name) }
        jsonPath("$.message") { value(message) }
    }
}
