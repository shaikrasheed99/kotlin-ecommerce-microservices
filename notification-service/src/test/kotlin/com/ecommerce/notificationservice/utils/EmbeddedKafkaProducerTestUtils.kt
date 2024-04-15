package com.ecommerce.notificationservice.utils

import com.ecommerce.notificationservice.events.OrderPlacedEvent
import io.cloudevents.CloudEvent
import io.cloudevents.core.builder.CloudEventBuilder
import io.cloudevents.core.provider.EventFormatProvider
import io.cloudevents.jackson.JsonFormat
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.http.MediaType
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime
import java.util.UUID

object EmbeddedKafkaProducerTestUtils {
    fun createTestProducer(embeddedKafkaBroker: EmbeddedKafkaBroker): Producer<String, String> {
        val producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker)
        addAdditionalProperties(embeddedKafkaBroker, producerProps)

        val producerFactory = DefaultKafkaProducerFactory<String, String>(producerProps)
        val producer = producerFactory.createProducer()

        return producer
    }

    private fun addAdditionalProperties(
        embeddedKafkaBroker: EmbeddedKafkaBroker,
        producerProps: MutableMap<String, Any>
    ) {
        producerProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = embeddedKafkaBroker.brokersAsString
        producerProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        producerProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    }

    fun createAndSerializeCloudEvent(event: String, eventType: String, source: String): String {
        val cloudEvent = createCloudEvent(event, eventType, source)
        val message = serializeCloudEvent(cloudEvent)

        return message
    }

    private fun createCloudEvent(event: String, eventType: String, source: String): CloudEvent {
        val currentTime = OffsetDateTime.now()

        return CloudEventBuilder.v1()
            .withId(UUID.randomUUID().toString())
            .withType(eventType)
            .withTime(currentTime)
            .withSource(URI.create(source))
            .withData(MediaType.APPLICATION_JSON.toString(), event.toByteArray())
            .build()
    }

    private fun serializeCloudEvent(cloudEvent: CloudEvent): String {
        return EventFormatProvider
            .getInstance()
            .resolveFormat(JsonFormat.CONTENT_TYPE)!!
            .serialize(cloudEvent).toString(StandardCharsets.UTF_8)
    }
}
