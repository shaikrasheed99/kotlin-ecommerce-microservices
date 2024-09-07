package com.ecommerce.orderservice.services

import com.ecommerce.orderservice.models.OutboxRepository
import com.ecommerce.orderservice.producer.EventPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PublishEventsService(
    private val outboxRepository: OutboxRepository,
    private val eventPublisher: EventPublisher,
    @Value("\${jobs.publish-events.batch-size}")
    private val batchSize: Int
) {
    @Transactional
    fun publish() {
        val pageOfSize = Pageable.ofSize(batchSize)
        val events = outboxRepository.findAll(pageOfSize)

        events.forEach {
            eventPublisher.publish(it)
            outboxRepository.delete(it)
        }
    }
}
