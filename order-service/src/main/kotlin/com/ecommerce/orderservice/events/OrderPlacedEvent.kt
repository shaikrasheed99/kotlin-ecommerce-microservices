package com.ecommerce.orderservice.events

import java.util.UUID

data class OrderPlacedEvent(
    var orderId: UUID,
    var skuCode: String,
    var quantity: Int
)
