package com.ecommerce.orderservice.dto.requests

import java.math.BigDecimal

data class OrderRequestBody(
    var skuCode: String,
    var price: BigDecimal,
    var quantity: Int
)
