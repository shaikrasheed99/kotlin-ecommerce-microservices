package com.ecommerce.notificationservice.integrationtests.controllers

import com.ecommerce.notificationservice.constants.MessageResponses
import com.ecommerce.notificationservice.constants.StatusResponses
import com.ecommerce.notificationservice.models.NotificationRepository
import com.ecommerce.notificationservice.utils.TestUtils
import com.ecommerce.notificationservice.utils.TestUtils.assertCommonResponseBody
import com.ecommerce.notificationservice.utils.TestUtils.getPostgreSQLContainer
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class NotificationControllerIntegrationTest {
    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

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
        val notification = TestUtils.createTestNotification()
        notificationRepository.save(notification)

        mockMvc.get("/notifications/recent")
            .andExpect {
                status { isOk() }
                assertCommonResponseBody(
                    status = StatusResponses.SUCCESS,
                    code = HttpStatus.OK,
                    message = MessageResponses.NOTIFICATION_FETCHED_SUCCESS.message
                )
            }

        notificationRepository.count() shouldBe 1
    }
}
