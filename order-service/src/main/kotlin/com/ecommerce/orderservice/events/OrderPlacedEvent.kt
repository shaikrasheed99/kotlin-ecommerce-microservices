package com.ecommerce.orderservice.events

import java.util.UUID

data class OrderPlacedEvent(
    var id: UUID,
    var skuCode: String,
)
