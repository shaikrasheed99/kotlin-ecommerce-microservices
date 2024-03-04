package com.ecommerce.notificationservice.integrationtests.models

import com.ecommerce.notificationservice.models.NotificationRepository
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
        val notification = createTestNotification()
        notificationRepository.save(notification)

        val notifications = notificationRepository.findRecentNotification()

        notifications[0].sender shouldBe notification.sender
        notifications[0].recipient shouldBe notification.recipient
        notifications[0].orderId shouldBe notification.orderId
        notifications[0].skuCode shouldBe notification.skuCode
    }
}
