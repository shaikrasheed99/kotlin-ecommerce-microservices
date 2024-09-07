package com.ecommerce.orderservice.utils

import com.ecommerce.orderservice.constants.Events
import com.ecommerce.orderservice.models.Outbox
import io.cloudevents.CloudEvent
import io.cloudevents.core.builder.CloudEventBuilder
import io.cloudevents.core.provider.EventFormatProvider
import io.cloudevents.jackson.JsonFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime

object CloudEventProcessor {
    fun createAndSerializeCloudEvent(event: Outbox): String {
        val cloudEvent = createCloudEvent(event)
        val message = serializeCloudEvent(cloudEvent)

        return message
    }

    private fun createCloudEvent(event: Outbox): CloudEvent {
        logger.info("Creating cloud event for event: $event")

        val currentTime = OffsetDateTime.now()

        return CloudEventBuilder.v1()
            .withId(event.eventId.toString())
            .withType(event.eventType)
            .withTime(currentTime)
            .withSource(URI.create(Events.ORDER_SERVICE))
            .withData(APPLICATION_JSON.toString(), event.eventPayload.toByteArray())
            .build()
    }

    private fun serializeCloudEvent(cloudEvent: CloudEvent): String {
        logger.info("Serializing the cloud event with id: ${cloudEvent.id}")

        return EventFormatProvider
            .getInstance()
            .resolveFormat(JsonFormat.CONTENT_TYPE)!!
            .serialize(cloudEvent).toString(StandardCharsets.UTF_8)
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
}
