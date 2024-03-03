package com.ecommerce.notificationservice.unittests.controllers

import com.ecommerce.notificationservice.constants.MessageResponses
import com.ecommerce.notificationservice.constants.StatusResponses
import com.ecommerce.notificationservice.controllers.NotificationController
import com.ecommerce.notificationservice.services.NotificationService
import com.ecommerce.notificationservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.notificationservice.utils.TestUtils.createTestNotification
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

class NotificationControllerTest : DescribeSpec({
    val mockNotificationService = mockk<NotificationService>()
    val notificationController = NotificationController(mockNotificationService)

    val notification = createTestNotification()

    describe("Notification Controller - annotations") {
        it("should have RestController & RequestMapping annotations to the Notification Controller class") {
            val classAnnotations = notificationController.javaClass.annotations
            val restControllerAnnotation = classAnnotations.firstOrNull { it is RestController } as RestController
            val requestMappingAnnotation = classAnnotations.firstOrNull { it is RequestMapping } as RequestMapping

            restControllerAnnotation shouldNotBe null

            requestMappingAnnotation shouldNotBe null
            requestMappingAnnotation.value.firstOrNull { it == "/notifications" } shouldNotBe null
        }
    }

    describe("Get Recent Notification") {
        it("should be able to get recent notification") {
            val notifications = listOf(notification)

            every { mockNotificationService.getRecentNotification() } returns notifications

            val response = notificationController.getRecentNotification().body

            response?.status shouldBe StatusResponses.SUCCESS
            response?.code shouldBe HttpStatus.OK
            response?.message shouldContain MessageResponses.NOTIFICATION_FETCHED_SUCCESS.message
            response?.data shouldBe notifications

            verify {
                mockNotificationService.getRecentNotification()
            }
        }
    }

    describe("Get Recent Notification - Error scenarios") {
        it("should be able to throw exception when service throws it") {
            val exception = Exception("exception from service")

            every { mockNotificationService.getRecentNotification() } throws exception

            shouldThrow<Exception> { notificationController.getRecentNotification() }

            verify {
                mockNotificationService.getRecentNotification()
            }
        }
    }

    describe("Get Recent Notification - annotations") {
        it("should have GetMapping annotation to the getRecentNotification method") {
            val annotations = notificationController.getMethodAnnotations("getRecentNotification")
            val getMappingAnnotation = annotations.firstOrNull { it is GetMapping } as GetMapping

            getMappingAnnotation shouldNotBe null
            getMappingAnnotation.value.firstOrNull { it == "/recent" } shouldNotBe null
        }
    }

    describe("Notification Controller - logger") {
        it("should initialize the logger of notification controller") {
            NotificationController.logger shouldNotBe null
        }
    }
})
