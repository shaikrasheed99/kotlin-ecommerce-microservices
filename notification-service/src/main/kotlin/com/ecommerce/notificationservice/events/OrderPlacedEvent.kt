package com.ecommerce.notificationservice.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class OrderPlacedEvent(
    @JsonProperty("orderId")
    var orderId: UUID,
    @JsonProperty("skuCode")
    var skuCode: String,
)
