package com.ecommerce.orderservice.unittests.models

import com.ecommerce.orderservice.models.Outbox
import com.ecommerce.orderservice.utils.EntityUtils.getAttributeAnnotations
import com.ecommerce.orderservice.utils.TestUtils.createOutbox
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

class OutboxTest : DescribeSpec({
    lateinit var outbox: Outbox

    describe("Outbox - getters") {
        it("should be able to have all the attributes to the Outbox class") {
            val eventId = UUID.randomUUID()
            outbox = createOutbox(id = eventId)
            outbox.eventId shouldBe eventId
            outbox.eventType shouldBe "test_type"
            outbox.eventPayload shouldBe "test_payload"
            outbox.topic shouldBe "test_topic"
        }
    }

    describe("Outbox - setters") {
        it("should be able to change Id attribute of the Outbox") {
            val eventId = UUID.randomUUID()
            outbox = createOutbox(id = eventId)

            outbox.eventId shouldBe eventId
        }

        it("should be able to change EventType attribute of the Outbox") {
            outbox.eventType = "another type"

            outbox.eventType shouldBe "another type"
        }

        it("should be able to change EventPayload attribute of the Outbox") {
            outbox.eventPayload = "another payload"

            outbox.eventPayload shouldBe "another payload"
        }

        it("should be able to change Topic attribute of the Outbox") {
            outbox.topic = "another topic"

            outbox.topic shouldBe "another topic"
        }
    }

    describe("Outbox - annotations") {
        it("should have Entity & Table annotations to Outbox class") {
            val classAnnotations = outbox.javaClass.annotations
            val entityAnnotation = classAnnotations.firstOrNull { it is Entity } as Entity
            val tableAnnotation = classAnnotations.firstOrNull { it is Table } as Table

            entityAnnotation shouldNotBe null

            tableAnnotation shouldNotBe null
            tableAnnotation.name shouldBe "outbox"
        }

        it("should have Id & GeneratedValue annotations to the Id attribute of Outbox class") {
            val annotations = outbox.getAttributeAnnotations("eventId")
            val idAnnotation = annotations.firstOrNull { it is Id } as Id
            val generatedValueAnnotation = annotations.firstOrNull { it is GeneratedValue } as GeneratedValue

            idAnnotation shouldNotBe null

            generatedValueAnnotation shouldNotBe null
            generatedValueAnnotation.strategy shouldBe GenerationType.UUID
        }
    }
})
