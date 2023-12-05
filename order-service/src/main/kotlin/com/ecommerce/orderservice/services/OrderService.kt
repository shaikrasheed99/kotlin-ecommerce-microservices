package com.ecommerce.orderservice.services

import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.dto.responses.InventoryResponse
import com.ecommerce.orderservice.dto.responses.Response
import com.ecommerce.orderservice.exceptions.InsufficientInventoryQuantityException
import com.ecommerce.orderservice.exceptions.InventoryNotAvailableException
import com.ecommerce.orderservice.exceptions.InventoryServiceErrorException
import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.models.OrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
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
                .bodyToMono(Response::class.java)
                .block()

            return inventoryResponse?.let {
                val inventory = ObjectMapper().convertValue(it.data, InventoryResponse::class.java)
                if (order.quantity <= inventory.quantity) {
                    orderRepository.save(order)
                } else {
                    val exceptionMessage = "Order cannot be created as it's quantity is more than inventory quantity"
                    throw InsufficientInventoryQuantityException(exceptionMessage)
                }
            }
        } catch (webClientResponseException: WebClientResponseException) {
            val exceptionMessage = "Inventory is not available for item with skuCode ${order.skuCode}"
            throw InventoryNotAvailableException(exceptionMessage)
        } catch (webClientRequestException: WebClientRequestException) {
            val exceptionMessage = "Internal server error from Inventory service"
            throw InventoryServiceErrorException(exceptionMessage)
        }
    }
}
