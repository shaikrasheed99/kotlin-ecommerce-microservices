package com.ecommerce.orderservice.configs

import com.ecommerce.orderservice.dto.responses.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class InventoryServiceClientImpl(
    private val webClientBuilder: WebClient.Builder
) : InventoryServiceClient {
    override fun getInventoryBySkuCode(skuCode: String): Response? {
        return webClientBuilder.build()
            .get()
            .uri("http://inventory-service/inventory/{sku-code}", skuCode)
            .retrieve()
            .bodyToMono(com.ecommerce.orderservice.dto.responses.Response::class.java)
            .block()
    }
}