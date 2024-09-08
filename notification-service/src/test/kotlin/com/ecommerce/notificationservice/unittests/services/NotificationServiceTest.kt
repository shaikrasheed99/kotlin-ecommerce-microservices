package com.ecommerce.notificationservice.unittests.services

import com.ecommerce.notificationservice.models.notification.Notification
import com.ecommerce.notificationservice.models.notification.NotificationRepository
import com.ecommerce.notificationservice.services.NotificationService
import com.ecommerce.notificationservice.utils.TestUtils.createTestNotification
import com.ecommerce.notificationservice.utils.TestUtils.createTestOrderPlacedEvent
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

            every { mockNotificationRepository.findFirstByOrderByCreatedAtDesc() } returns notifications

            val recentNotification = notificationService.getRecentNotification()

            recentNotification[0]

            verify {
                mockNotificationRepository.findFirstByOrderByCreatedAtDesc()
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
            every { mockNotificationRepository.findFirstByOrderByCreatedAtDesc() } throws exception

            shouldThrow<Exception> {
                notificationService.getRecentNotification()
            }

            verify {
                mockNotificationRepository.findFirstByOrderByCreatedAtDesc()
            }
        }
    }

    describe("Save Notification with Order Placed Event") {
        it("should be able to save notification with order placed event data") {
            val orderPlacedEvent = createTestOrderPlacedEvent()
            val notification = createTestNotification()

            every { mockNotificationRepository.save(any(Notification::class)) } returns notification

            notificationService.saveNotificationWith(orderPlacedEvent)

            verify { mockNotificationRepository.save(any(Notification::class)) }
        }
    }

    describe("Save Notification with Order Placed Event - Error scenarios") {
        it("should be able to throw Exception when repository throws exception") {
            val orderPlacedEvent = createTestOrderPlacedEvent()
            val exception = Exception("exception from repository layer")
            every { mockNotificationRepository.save(any(Notification::class)) } throws exception

            shouldThrow<Exception> {
                notificationService.saveNotificationWith(orderPlacedEvent)
            }

            verify {
                mockNotificationRepository.save(any(Notification::class))
            }
        }
    }
})
