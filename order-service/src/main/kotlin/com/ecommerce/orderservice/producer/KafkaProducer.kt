package com.ecommerce.orderservice.producer

import com.ecommerce.orderservice.events.OrderPlacedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, OrderPlacedEvent>) {
    fun sendOrderPlacedEvent(topic: String, event: OrderPlacedEvent) {
        logger.info("Sending event: $event to topic: $topic")
        kafkaTemplate.send(topic, event)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
