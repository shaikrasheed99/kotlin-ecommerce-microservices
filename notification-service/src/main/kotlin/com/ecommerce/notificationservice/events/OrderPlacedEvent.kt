package com.ecommerce.notificationservice.events

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class OrderPlacedEvent(
    @JsonProperty("orderId")
    var orderId: UUID,
    @JsonProperty("skuCode")
    var skuCode: String,
)
