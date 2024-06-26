package com.ecommerce.notificationservice.utils

import com.ecommerce.notificationservice.constants.StatusResponses
import com.ecommerce.notificationservice.events.OrderPlacedEvent
import com.ecommerce.notificationservice.models.Notification
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

object TestUtils {
    fun createTestNotification(
        createdAt: Timestamp = Timestamp.from(Instant.now())
    ) = Notification(
        id = UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10"),
        sender = "test@gmail.com",
        recipient = "test@gmail.com",
        isSent = false,
        orderId = UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10"),
        skuCode = "test_skucode",
        createdAt = createdAt
    )

    fun createTestOrderPlacedEvent(): OrderPlacedEvent = OrderPlacedEvent(
        orderId = UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10"),
        skuCode = "test_skuCode"
    )

    fun getPostgreSQLContainer(): PostgreSQLContainer<*>? =
        PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("notifications")
            .withInitScript("create-notifications-table.sql")

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
