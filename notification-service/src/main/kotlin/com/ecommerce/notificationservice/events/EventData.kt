package com.ecommerce.notificationservice.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class EventData(
    val data: OrderPlacedEvent
)
