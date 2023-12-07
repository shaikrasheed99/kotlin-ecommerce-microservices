package com.ecommerce.orderservice.configs

import com.ecommerce.orderservice.dto.responses.Response

interface InventoryServiceClient {
    fun getInventoryBySkuCode(skuCode: String): Response?
}