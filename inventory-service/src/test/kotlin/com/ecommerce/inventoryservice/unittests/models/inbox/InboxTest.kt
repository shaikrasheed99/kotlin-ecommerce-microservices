package com.ecommerce.inventoryservice.unittests.models.inbox

import com.ecommerce.inventoryservice.models.inbox.Inbox
import com.ecommerce.inventoryservice.utils.EntityUtils.getAttributeAnnotations
import com.ecommerce.inventoryservice.utils.TestUtils.createInbox
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

class InboxTest : DescribeSpec({
    lateinit var inbox: Inbox

    describe("Inbox - getters") {
        it("should be able to have all the attributes to the Inbox class") {
            val id = UUID.randomUUID()
            val eventId = UUID.randomUUID()
            inbox = createInbox(id = id, eventId = eventId)
            inbox.id shouldBe id
            inbox.eventId shouldBe eventId
            inbox.eventType shouldBe "test_type"
            inbox.topic shouldBe "test_topic"
        }
    }

    describe("Inbox - setters") {
        it("should be able to change Id attribute of the Inbox") {
            val id = UUID.randomUUID()
            inbox = createInbox()
            inbox.id = id

            inbox.id shouldBe id
        }

        it("should be able to change EventId attribute of the Inbox") {
            val eventId = UUID.randomUUID()
            inbox = createInbox()
            inbox.eventId = eventId

            inbox.eventId shouldBe eventId
        }

        it("should be able to change EventType attribute of the Inbox") {
            inbox = createInbox()
            inbox.eventType = "another type"

            inbox.eventType shouldBe "another type"
        }

        it("should be able to change Topic attribute of the Inbox") {
            inbox = createInbox()
            inbox.topic = "another topic"

            inbox.topic shouldBe "another topic"
        }
    }

    describe("Inbox - annotations") {
        it("should have Entity & Table annotations to Inbox class") {
            val classAnnotations = inbox.javaClass.annotations
            val entityAnnotation = classAnnotations.firstOrNull { it is Entity } as Entity
            val tableAnnotation = classAnnotations.firstOrNull { it is Table } as Table

            entityAnnotation shouldNotBe null

            tableAnnotation shouldNotBe null
            tableAnnotation.name shouldBe "inbox"
        }

        it("should have Id & GeneratedValue annotations to the Id attribute of Inbox class") {
            val annotations = inbox.getAttributeAnnotations("id")
            val idAnnotation = annotations.firstOrNull { it is Id } as Id
            val generatedValueAnnotation = annotations.firstOrNull { it is GeneratedValue } as GeneratedValue

            idAnnotation shouldNotBe null

            generatedValueAnnotation shouldNotBe null
            generatedValueAnnotation.strategy shouldBe GenerationType.UUID
        }
    }
})
