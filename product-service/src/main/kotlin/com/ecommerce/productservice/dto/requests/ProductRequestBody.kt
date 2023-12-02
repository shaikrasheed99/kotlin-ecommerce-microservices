package com.ecommerce.productservice.dto.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class ProductRequestBody(
    @field:NotBlank(message = "Request Product name should not be blank")
    var name: String,

    @field:NotBlank(message = "Request Product description should not be blank")
    var description: String,

    @field:Positive(message = "Request Product price should be greater than zero")
    var price: BigDecimal
)
