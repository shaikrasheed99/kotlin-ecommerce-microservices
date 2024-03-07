package com.ecommerce.notificationservice.utils

import com.ecommerce.notificationservice.events.OrderPlacedEvent
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils

object EmbeddedKafkaProducerTestUtils {
    fun createTestProducer(embeddedKafkaBroker: EmbeddedKafkaBroker): Producer<String, OrderPlacedEvent> {
        val producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker)
        addAdditionalProperties(embeddedKafkaBroker, producerProps)

        val producerFactory = DefaultKafkaProducerFactory<String, OrderPlacedEvent>(producerProps)
        val producer = producerFactory.createProducer()

        return producer
    }

    private fun addAdditionalProperties(
        embeddedKafkaBroker: EmbeddedKafkaBroker,
        producerProps: MutableMap<String, Any>
    ) {
        producerProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = embeddedKafkaBroker.brokersAsString
        producerProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        producerProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
    }
}
