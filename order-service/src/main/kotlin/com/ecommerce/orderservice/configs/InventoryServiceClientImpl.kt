package com.ecommerce.orderservice.configs

import com.ecommerce.orderservice.dto.responses.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class InventoryServiceClientImpl(
    private val webClientBuilder: WebClient.Builder
) : InventoryServiceClient {
    @Value("\${inventory.service.url}")
    private lateinit var inventoryServiceBaseUrl: String

    override fun getInventoryBySkuCode(skuCode: String): Response? {
        val url = "$inventoryServiceBaseUrl/inventory/{sku-code}"

        return webClientBuilder.build()
            .get()
            .uri(url, skuCode)
            .retrieve()
            .bodyToMono(com.ecommerce.orderservice.dto.responses.Response::class.java)
            .block()
    }
}