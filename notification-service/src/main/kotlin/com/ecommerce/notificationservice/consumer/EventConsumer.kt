package com.ecommerce.notificationservice.consumer

import com.ecommerce.notificationservice.constants.EventTypes.ORDER_PLACED
import com.ecommerce.notificationservice.events.EventData
import com.ecommerce.notificationservice.events.OrderPlacedEvent
import com.ecommerce.notificationservice.services.NotificationService
import com.ecommerce.notificationservice.utils.CloudEventProcessor.deserializeCloudEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class EventConsumer(private val notificationService: NotificationService) {
    @KafkaListener(topics = ["\${spring.kafka.topic}"])
    fun consume(
        payload: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String
    ) {
        logger.info("Consumed the event from topic: $topic")

        val cloudEvent = deserializeCloudEvent(payload)

        when (cloudEvent.type) {
            ORDER_PLACED.type -> handleOrderPlacedEvent(payload)
        }
    }

    private fun handleOrderPlacedEvent(payload: String) {
        val orderPlacedEvent = deserializeOrderPlacedEvent(payload)

        logger.info(
            "Received event with order id: ${orderPlacedEvent.orderId} and skucode: ${orderPlacedEvent.skuCode}"
        )

        notificationService.saveNotificationWith(orderPlacedEvent)
    }

    private fun deserializeOrderPlacedEvent(payload: String): OrderPlacedEvent {
        logger.info("Deserializing the order placed event from payload: $payload")

        val mapper = ObjectMapper().registerModules(KotlinModule.Builder().build())
        val eventData = mapper.readValue(payload, EventData::class.java)

        return eventData.data
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
