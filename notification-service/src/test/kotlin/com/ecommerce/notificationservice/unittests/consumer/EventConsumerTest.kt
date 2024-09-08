package com.ecommerce.notificationservice.unittests.consumer

import com.ecommerce.notificationservice.constants.EventTypes
import com.ecommerce.notificationservice.consumer.EventConsumer
import com.ecommerce.notificationservice.events.OrderPlacedEvent
import com.ecommerce.notificationservice.models.Inbox
import com.ecommerce.notificationservice.models.InboxRepository
import com.ecommerce.notificationservice.services.NotificationService
import com.ecommerce.notificationservice.utils.EmbeddedKafkaProducerTestUtils.createAndSerializeCloudEvent
import com.ecommerce.notificationservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.notificationservice.utils.TestUtils.createInbox
import com.ecommerce.notificationservice.utils.TestUtils.createTestOrderPlacedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

private const val TEST_TOPIC = "testTopic"
private const val TEST_SOURCE = "test_source"

class EventConsumerTest : DescribeSpec({
    val mockNotificationService = mockk<NotificationService>()
    val mockInboxRepository = mockk<InboxRepository>()
    val eventConsumer = EventConsumer(mockNotificationService, mockInboxRepository)

    val mapper = ObjectMapper()

    afterEach {
        clearAllMocks()
        unmockkAll()
    }

    describe("Event Consumer - annotations") {
        it("should have Component annotation to the event consumer class") {
            val annotations = eventConsumer.javaClass.annotations
            val componentAnnotation = annotations.firstOrNull { it is Component } as Component

            componentAnnotation shouldNotBe null
        }
    }

    describe("consume") {
        it("should be able to save order placed events to notifications table") {
            val orderPlacedEventJson = mapper.writeValueAsString(createTestOrderPlacedEvent())
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            every { mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class)) } just runs
            every { mockInboxRepository.findByEventId(any(UUID::class)) } returns Optional.empty()
            every { mockInboxRepository.save(any(Inbox::class)) } returns createInbox()

            eventConsumer.consume(payload, TEST_TOPIC)

            verify {
                mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class))
                mockInboxRepository.findByEventId(any(UUID::class))
                mockInboxRepository.save(any(Inbox::class))
            }
        }

        it("should be able to skip event when it is already present in inbox table") {
            val orderPlacedEventJson = mapper.writeValueAsString(createTestOrderPlacedEvent())
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            every { mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class)) } just runs
            every { mockInboxRepository.findByEventId(any(UUID::class)) } returns Optional.of(createInbox())
            every { mockInboxRepository.save(any(Inbox::class)) } returns createInbox()

            eventConsumer.consume(payload, TEST_TOPIC)

            verify { mockInboxRepository.findByEventId(any(UUID::class)) }
            verify(exactly = 0) {
                mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class))
                mockInboxRepository.save(any(Inbox::class))
            }
        }

        it("should be able to skip events which are not order placed events") {
            val orderPlacedEventJson = mapper.writeValueAsString(createTestOrderPlacedEvent())
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = "testEventType",
                source = TEST_SOURCE
            )
            every { mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class)) } just runs
            every { mockInboxRepository.findByEventId(any(UUID::class)) } returns Optional.empty()
            every { mockInboxRepository.save(any(Inbox::class)) } returns createInbox()

            shouldNotThrowAny {
                eventConsumer.consume(payload, TEST_TOPIC)
            }

            verify {
                mockInboxRepository.findByEventId(any(UUID::class))
                mockInboxRepository.save(any(Inbox::class))
            }
            verify(exactly = 0) {
                mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class))
            }
        }
    }

    describe("consume - Error scenarios") {
        it("should be able to throw Exception when error occurs while fetching the inbox") {
            val orderPlacedEventJson = mapper.writeValueAsString(createTestOrderPlacedEvent())
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            val exception = Exception("Failed to fetch the inbox events")
            every { mockInboxRepository.findByEventId(any(UUID::class)) } throws exception
            every { mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class)) } just runs
            every { mockInboxRepository.save(any(Inbox::class)) } returns createInbox()

            val thrownException = shouldThrow<Exception> {
                eventConsumer.consume(payload, TEST_TOPIC)
            }

            thrownException.message shouldBe exception.message

            verify { mockInboxRepository.findByEventId(any(UUID::class)) }
            verify(exactly = 0) {
                mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class))
                mockInboxRepository.save(any(Inbox::class))
            }
        }

        it("should be able to throw MismatchedInputException when invalid order placed event is passed") {
            val invalidOrderPlacedEventJson = mapper.writeValueAsString("invalid order placed event")
            val payload = createAndSerializeCloudEvent(
                event = invalidOrderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            every { mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class)) } just runs
            every { mockInboxRepository.findByEventId(any(UUID::class)) } returns Optional.empty()
            every { mockInboxRepository.save(any(Inbox::class)) } returns createInbox()

            shouldThrow<MismatchedInputException> {
                eventConsumer.consume(payload, TEST_TOPIC)
            }

            verify { mockInboxRepository.findByEventId(any(UUID::class)) }
            verify(exactly = 0) {
                mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class))
                mockInboxRepository.save(any(Inbox::class))
            }
        }

        it("should be able to throw Exception when service layer throws any exception") {
            val orderPlacedEventJson = mapper.writeValueAsString(createTestOrderPlacedEvent())
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            val exception = Exception("exception from notification repository layer")
            every { mockInboxRepository.findByEventId(any(UUID::class)) } returns Optional.empty()
            every { mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class)) } throws exception
            every { mockInboxRepository.save(any(Inbox::class)) } throws exception

            val thrownException = shouldThrow<Exception> {
                eventConsumer.consume(payload, TEST_TOPIC)
            }

            thrownException.message shouldBe exception.message

            verify {
                mockInboxRepository.findByEventId(any(UUID::class))
                mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class))
            }
            verify(exactly = 0) { mockInboxRepository.save(any(Inbox::class)) }
        }

        it("should be able to throw Exception when error occurs while saving the inbox") {
            val orderPlacedEventJson = mapper.writeValueAsString(createTestOrderPlacedEvent())
            val payload = createAndSerializeCloudEvent(
                event = orderPlacedEventJson,
                eventType = EventTypes.ORDER_PLACED.type,
                source = TEST_SOURCE
            )
            val exception = Exception("Failed to save the inbox events")
            every { mockInboxRepository.findByEventId(any(UUID::class)) } returns Optional.empty()
            every { mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class)) } just runs
            every { mockInboxRepository.save(any(Inbox::class)) } throws exception

            val thrownException = shouldThrow<Exception> {
                eventConsumer.consume(payload, TEST_TOPIC)
            }

            thrownException.message shouldBe exception.message

            verify {
                mockInboxRepository.findByEventId(any(UUID::class))
                mockNotificationService.saveNotificationWith(any(OrderPlacedEvent::class))
                mockInboxRepository.save(any(Inbox::class))
            }
        }
    }

    describe("consume - annotations") {
        it("should have KafkaListener annotation to the consume method") {
            val methodAnnotations = eventConsumer.getMethodAnnotations("consume")
            val kafkaListenerAnnotation = methodAnnotations.firstOrNull { it is KafkaListener } as KafkaListener

            kafkaListenerAnnotation shouldNotBe null
            kafkaListenerAnnotation.topics.first() shouldBe "\${spring.kafka.topic}"
        }
    }

    describe("Event Consumer - logger") {
        it("should initialize the logger of event consumer") {
            EventConsumer.logger shouldNotBe null
        }
    }
})
