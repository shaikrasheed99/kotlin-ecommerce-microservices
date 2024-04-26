package com.ecommerce.inventoryservice.events

import com.ecommerce.inventoryservice.events.OrderPlacedEvent
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class EventData(
    val data: OrderPlacedEvent
)
