package com.ecommerce.orderservice.dto.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class OrderRequestBody(
    @field:NotBlank(message = "Requested Sku Code should not be blank")
    var skuCode: String,

    @field:Positive(message = "Requested Price should not be negative")
    var price: BigDecimal,

    @field:Positive(message = "Requested Quantity should not be negative")
    var quantity: Int
)
