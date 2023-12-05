package com.ecommerce.orderservice.dto.responses

data class InventoryResponse(
    var id: Int = 0,
    var skuCode: String = "",
    var quantity: Int = 0
)