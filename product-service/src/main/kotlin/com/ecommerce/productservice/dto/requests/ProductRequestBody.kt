package com.ecommerce.productservice.dto.requests

import java.math.BigDecimal

data class ProductRequestBody(
    var name: String,
    var description: String,
    var price: BigDecimal
)
