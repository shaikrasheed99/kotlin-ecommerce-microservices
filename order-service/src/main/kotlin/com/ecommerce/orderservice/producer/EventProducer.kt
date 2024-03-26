package com.ecommerce.orderservice.producer

import com.ecommerce.orderservice.events.OrderPlacedEvent
import com.ecommerce.orderservice.utils.CloudEventProcessor.createAndSerializeCloudEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class EventProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {
    fun sendOrderPlacedEvent(topic: String, orderPlacedEvent: OrderPlacedEvent) {
        logger.info("Sending event: $orderPlacedEvent to topic: $topic")

        val eventJsonString = convertEventToJsonString(orderPlacedEvent)
        val message = createAndSerializeCloudEvent(eventJsonString)

        kafkaTemplate.send(topic, message)

        logger.info("Produced event: $orderPlacedEvent to topic: $topic")
    }

    private fun convertEventToJsonString(orderPlacedEvent: OrderPlacedEvent): String {
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(orderPlacedEvent)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
