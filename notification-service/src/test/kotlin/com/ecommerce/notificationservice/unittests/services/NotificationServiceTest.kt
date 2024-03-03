package com.ecommerce.notificationservice.unittests.services

import com.ecommerce.notificationservice.models.NotificationRepository
import com.ecommerce.notificationservice.services.NotificationService
import com.ecommerce.notificationservice.utils.TestUtils.createTestNotification
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.stereotype.Service

class NotificationServiceTest : DescribeSpec({
    val mockNotificationRepository = mockk<NotificationRepository>()
    val notificationService = NotificationService(mockNotificationRepository)

    describe("Notification Service - annotations") {
        it("should have Service annotation to the Notification Service class") {
            val classAnnotations = notificationService.javaClass.annotations
            val serviceAnnotation = classAnnotations.firstOrNull { it is Service } as Service

            serviceAnnotation shouldNotBe null
        }
    }

    describe("Get Recent Notification") {
        it("should be able to get recent notification") {
            val notifications = listOf(createTestNotification())

            every { mockNotificationRepository.findRecentNotification() } returns notifications

            val recentNotification = notificationService.getRecentNotification()

            recentNotification[0]

            verify {
                mockNotificationRepository.findRecentNotification()
            }

            recentNotification[0].id shouldBe notifications[0].id
            recentNotification[0].sender shouldBe notifications[0].sender
            recentNotification[0].recipient shouldBe notifications[0].recipient
            recentNotification[0].isSent shouldBe notifications[0].isSent
            recentNotification[0].orderId shouldBe notifications[0].orderId
            recentNotification[0].skuCode shouldBe notifications[0].skuCode
        }
    }

    describe("Get Recent Notification - Error scenarios") {
        it("should be able to throw Exception when it is thrown from repository") {
            val exception = Exception("exception from repository layer")
            every { mockNotificationRepository.findRecentNotification() } throws exception

            shouldThrow<Exception> {
                notificationService.getRecentNotification()
            }

            verify {
                mockNotificationRepository.findRecentNotification()
            }
        }
    }
})
