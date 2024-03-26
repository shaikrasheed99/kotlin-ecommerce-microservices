package com.ecommerce.orderservice.utils

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
import java.util.UUID

private const val ORDER_PLACED_EVENT_TYPE = "order_placed_event"
private const val ORDER_SERVICE = "order_service"

object CloudEventProcessor {
    fun createCloudEvent(event: String): CloudEvent {
        logger.info("Creating cloud event for event: $event")

        val currentTime = OffsetDateTime.now()

        return CloudEventBuilder.v1()
            .withId(UUID.randomUUID().toString())
            .withType(ORDER_PLACED_EVENT_TYPE)
            .withTime(currentTime)
            .withSource(URI.create(ORDER_SERVICE))
            .withData(APPLICATION_JSON.toString(), event.toByteArray())
            .build()
    }

    fun serializeCloudEvent(cloudEvent: CloudEvent): String {
        logger.info("Serializing the cloud event with id: ${cloudEvent.id}")

        return EventFormatProvider
            .getInstance()
            .resolveFormat(JsonFormat.CONTENT_TYPE)!!
            .serialize(cloudEvent).toString(StandardCharsets.UTF_8)
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
}