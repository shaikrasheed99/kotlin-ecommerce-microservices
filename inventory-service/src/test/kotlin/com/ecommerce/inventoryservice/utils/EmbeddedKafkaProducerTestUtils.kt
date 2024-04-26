package com.ecommerce.inventoryservice.utils

import io.cloudevents.CloudEvent
import io.cloudevents.core.builder.CloudEventBuilder
import io.cloudevents.core.provider.EventFormatProvider
import io.cloudevents.jackson.JsonFormat
import org.springframework.http.MediaType
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime
import java.util.UUID

object EmbeddedKafkaProducerTestUtils {
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
