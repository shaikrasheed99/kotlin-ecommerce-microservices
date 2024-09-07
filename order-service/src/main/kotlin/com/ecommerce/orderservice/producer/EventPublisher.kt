package com.ecommerce.orderservice.producer

import com.ecommerce.orderservice.models.Outbox
import com.ecommerce.orderservice.utils.CloudEventProcessor.createAndSerializeCloudEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class EventPublisher(private val kafkaTemplate: KafkaTemplate<String, String>) {
    fun publish(event: Outbox) {
        logger.info("Publishing event: ${event.eventPayload} to topic: ${event.topic}")

        val message = createAndSerializeCloudEvent(event)
        kafkaTemplate.send(event.topic, message)

        logger.info("Published event: ${event.eventPayload} to topic: ${event.topic}")
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
