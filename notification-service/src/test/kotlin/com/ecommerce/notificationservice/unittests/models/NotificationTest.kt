package com.ecommerce.notificationservice.unittests.models

import com.ecommerce.notificationservice.models.Notification
import com.ecommerce.notificationservice.utils.EntityUtils.getAttributeAnnotations
import com.ecommerce.notificationservice.utils.TestUtils.createTestNotification
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp
import java.util.UUID

class NotificationTest : DescribeSpec({
    lateinit var notification: Notification

    beforeEach {
        notification = createTestNotification(
            createdAt = Timestamp.valueOf("2024-03-02 22:34:55.239334")
        )
    }

    describe("Notification - getters") {
        it("should be able to have all the attributes to the Notification class") {
            notification.id shouldBe UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10")
            notification.sender shouldBe "test@gmail.com"
            notification.recipient shouldBe "test@gmail.com"
            notification.isSent shouldBe false
            notification.orderId shouldBe UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10")
            notification.skuCode shouldBe "test_skucode"
            notification.createdAt shouldBe Timestamp.valueOf("2024-03-02 22:34:55.239334")
        }
    }

    describe("Notification - setters") {
        it("should be able to change Id attribute of the Notification") {
            notification.id = UUID.fromString("bbb8a937-0504-4468-823a-04ccd6964d10")

            notification.id shouldBe UUID.fromString("bbb8a937-0504-4468-823a-04ccd6964d10")
        }

        it("should be able to change Sender attribute of the Notification") {
            notification.sender = "another@gmail.com"

            notification.sender shouldBe "another@gmail.com"
        }

        it("should be able to change Recipient attribute of the Notification") {
            notification.recipient = "another@gmail.com"

            notification.recipient shouldBe "another@gmail.com"
        }

        it("should be able to change IsSent attribute of the Notification") {
            notification.isSent = true

            notification.isSent shouldBe true
        }

        it("should be able to change OrderId attribute of the Notification") {
            notification.orderId = UUID.fromString("bbb8a937-0504-4468-823a-04ccd6964d10")

            notification.orderId shouldBe UUID.fromString("bbb8a937-0504-4468-823a-04ccd6964d10")
        }

        it("should be able to change SkuCode attribute of the Notification") {
            notification.skuCode = "another skuCode"

            notification.skuCode shouldBe "another skuCode"
        }

        it("should be able to change CreatedAt attribute of the Notification") {
            notification.createdAt = Timestamp.valueOf("2024-03-02 22:34:55.239334")

            notification.createdAt shouldBe Timestamp.valueOf("2024-03-02 22:34:55.239334")
        }
    }

    describe("Notification - annotations") {
        it("should have Entity & Table annotations to Notification class") {
            val classAnnotations = notification.javaClass.annotations
            val entityAnnotation = classAnnotations.firstOrNull { it is Entity } as Entity
            val tableAnnotation = classAnnotations.firstOrNull { it is Table } as Table

            entityAnnotation shouldNotBe null

            tableAnnotation shouldNotBe null
            tableAnnotation.name shouldBe "notifications"
        }

        it("should have Id & GeneratedValue annotations to the Id attribute of Notification class") {
            val annotations = notification.getAttributeAnnotations("id")
            val idAnnotation = annotations.firstOrNull { it is Id } as Id
            val generatedValueAnnotation = annotations.firstOrNull { it is GeneratedValue } as GeneratedValue

            idAnnotation shouldNotBe null

            generatedValueAnnotation shouldNotBe null
            generatedValueAnnotation.strategy shouldBe GenerationType.UUID
        }
    }
})
