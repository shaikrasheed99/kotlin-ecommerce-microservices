package com.ecommerce.orderservice.unittests.services

import com.ecommerce.orderservice.models.Outbox
import com.ecommerce.orderservice.models.OutboxRepository
import com.ecommerce.orderservice.producer.EventPublisher
import com.ecommerce.orderservice.services.PublishEventsService
import com.ecommerce.orderservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.orderservice.utils.TestUtils.createOutbox
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

class PublishEventsServiceTest : DescribeSpec({
    val mockOutboxRepository = mockk<OutboxRepository>()
    val mockEventPublisher = mockk<EventPublisher>()
    val batchSize = 2

    val publishEventsService = PublishEventsService(mockOutboxRepository, mockEventPublisher, batchSize)

    afterEach {
        clearAllMocks()
        unmockkAll()
    }

    describe("PublishEventsService - annotations") {
        it("should have Service annotation to the PublishEventsService class") {
            val classAnnotations = publishEventsService.javaClass.annotations
            val serviceAnnotation = classAnnotations.firstOrNull { it is Service } as Service

            serviceAnnotation shouldNotBe null
        }
    }

    describe("PublishEventsService - publish") {
        it("should publish the events from the outbox and delete the records") {
            val events = listOf(createOutbox())
            every { mockOutboxRepository.findAll(any(Pageable::class)) } returns PageImpl(events)
            every { mockEventPublisher.publish(any(Outbox::class)) } just runs
            every { mockOutboxRepository.delete(any(Outbox::class)) } just runs

            publishEventsService.publish()

            verify(exactly = 1) {
                mockOutboxRepository.findAll(any(Pageable::class))
                mockEventPublisher.publish(any(Outbox::class))
                mockOutboxRepository.delete(any(Outbox::class))
            }
        }
    }

    describe("PublishEventsService - publish - Error scenarios") {
        it("should throw exception when error occurs while fetching outbox events") {
            val exception = RuntimeException("Failed to fetch the outbox events")
            every { mockOutboxRepository.findAll(any(Pageable::class)) } throws exception
            every { mockEventPublisher.publish(any(Outbox::class)) } just runs
            every { mockOutboxRepository.delete(any(Outbox::class)) } just runs

            val actualException = shouldThrow<RuntimeException> { publishEventsService.publish() }

            actualException.message shouldBe exception.message
            verify(exactly = 1) { mockOutboxRepository.findAll(any(Pageable::class)) }
            verify(exactly = 0) {
                mockEventPublisher.publish(any(Outbox::class))
                mockOutboxRepository.delete(any(Outbox::class))
            }
        }

        it("should throw exception when error occurs while publishing outbox events") {
            val exception = RuntimeException("Failed to publish the outbox events")
            val events = listOf(createOutbox())
            every { mockOutboxRepository.findAll(any(Pageable::class)) } returns PageImpl(events)
            every { mockEventPublisher.publish(any(Outbox::class)) } throws exception
            every { mockOutboxRepository.delete(any(Outbox::class)) } just runs

            val actualException = shouldThrow<RuntimeException> { publishEventsService.publish() }

            actualException.message shouldBe exception.message
            verify(exactly = 1) {
                mockOutboxRepository.findAll(any(Pageable::class))
                mockEventPublisher.publish(any(Outbox::class))
            }
            verify(exactly = 0) { mockOutboxRepository.delete(any(Outbox::class)) }
        }

        it("should throw exception when error occurs while deleting outbox events") {
            val exception = RuntimeException("Failed to delete the outbox events")
            val events = listOf(createOutbox())
            every { mockOutboxRepository.findAll(any(Pageable::class)) } returns PageImpl(events)
            every { mockEventPublisher.publish(any(Outbox::class)) } just runs
            every { mockOutboxRepository.delete(any(Outbox::class)) } throws exception

            val actualException = shouldThrow<RuntimeException> { publishEventsService.publish() }

            actualException.message shouldBe exception.message
            verify(exactly = 1) {
                mockOutboxRepository.findAll(any(Pageable::class))
                mockEventPublisher.publish(any(Outbox::class))
                mockOutboxRepository.delete(any(Outbox::class))
            }
        }
    }

    describe("PublishEventsService - publish - annotations") {
        it("should have Transactional annotation to the publish method") {
            val annotations = publishEventsService.getMethodAnnotations("publish")
            val transactionalAnnotation = annotations.firstOrNull { it is Transactional } as Transactional

            transactionalAnnotation shouldNotBe null
        }
    }
})
