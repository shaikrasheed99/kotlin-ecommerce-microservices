package com.ecommerce.notificationservice.consumer

import com.ecommerce.notificationservice.events.OrderPlacedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer {
    @KafkaListener(topics = ["notificationsTopic"])
    fun handleOrderPlacedEvent(orderPlacedEvent: OrderPlacedEvent) {
        logger.info(
            "Received event with order id: ${orderPlacedEvent.id} and skucode: ${orderPlacedEvent.skuCode}"
        )
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}