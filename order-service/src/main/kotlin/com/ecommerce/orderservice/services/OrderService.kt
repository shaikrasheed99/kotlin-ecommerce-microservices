package com.ecommerce.orderservice.services

import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.dto.responses.SuccessResponse
import com.ecommerce.orderservice.exceptions.InventoryNotFoundException
import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.models.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val webClient: WebClient
) {
    fun createOrder(orderRequestBody: OrderRequestBody): Order? {
        val order = Order(
            id = null,
            skuCode = orderRequestBody.skuCode,
            price = orderRequestBody.price,
            quantity = orderRequestBody.quantity
        )

        try {
            val inventoryResponse = webClient
                .get()
                .uri("http://localhost:8082/inventory/{sku-code}", order.skuCode)
                .retrieve()
                .bodyToMono(SuccessResponse::class.java)
                .block()

            return inventoryResponse?.let {
                orderRepository.save(order)
            }
        } catch (exception: WebClientResponseException) {
            throw InventoryNotFoundException("Inventory is not available for item with skuCode ${order.skuCode}")
        }
    }
}
