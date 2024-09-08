package com.ecommerce.notificationservice.integrationtests.models

import com.ecommerce.notificationservice.models.notification.NotificationRepository
import com.ecommerce.notificationservice.utils.TestUtils.createTestNotification
import com.ecommerce.notificationservice.utils.TestUtils.getPostgreSQLContainer
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class NotificationRepositoryTest {
    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    companion object {
        @Container
        @ServiceConnection
        val postgreSQLContainer = getPostgreSQLContainer()
    }

    @AfterEach
    fun tearDown() {
        notificationRepository.deleteAll()
    }

    @Test
    internal fun shouldBeAbleToReturnRecentNotification() {
        val notification = createTestNotification(
            skuCode = "first_notification_skuCode"
        )
        notificationRepository.save(notification)

        val recentNotification = createTestNotification(
            skuCode = "recent_notification_skuCode"
        )
        notificationRepository.save(recentNotification)

        val notifications = notificationRepository.findFirstByOrderByCreatedAtDesc()

        notifications[0].skuCode shouldBe recentNotification.skuCode
    }
}
