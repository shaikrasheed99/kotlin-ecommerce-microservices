package com.ecommerce.inventoryservice.utils

import io.cloudevents.CloudEvent
import io.cloudevents.core.provider.EventFormatProvider
import io.cloudevents.jackson.JsonFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

object CloudEventProcessor {
    fun deserializeCloudEvent(payload: String): CloudEvent {
        logger.info("Deserializing the cloud event from payload: $payload")

        return EventFormatProvider
            .getInstance()
            .resolveFormat(JsonFormat.CONTENT_TYPE)!!
            .deserialize(payload.toByteArray(StandardCharsets.UTF_8))
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
}
