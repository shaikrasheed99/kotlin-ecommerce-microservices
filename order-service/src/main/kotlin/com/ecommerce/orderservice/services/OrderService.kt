package com.ecommerce.orderservice.services

import com.ecommerce.orderservice.configs.InventoryServiceClient
import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.dto.responses.InventoryResponse
import com.ecommerce.orderservice.exceptions.InsufficientInventoryQuantityException
import com.ecommerce.orderservice.exceptions.InventoryServiceErrorException
import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.models.OrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val inventoryServiceClient: InventoryServiceClient
) {
    @CircuitBreaker(name = "inventoryClient")
    @Retry(name = "inventoryClient", fallbackMethod = "handleCreateOrderRetryFailure")
    fun createOrder(orderRequestBody: OrderRequestBody): Order? {
        var order: Order? = null

        runCatching {
            inventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
        }.onSuccess { inventoryResponse ->
            order = inventoryResponse?.let {
                val inventory = mapper.convertValue(it.data, InventoryResponse::class.java)
                if (orderRequestBody.quantity <= inventory.quantity) {
                    orderRepository.save(mapToOrder(orderRequestBody))
                } else {
                    val exceptionMessage =
                        "Order cannot be created as it's quantity is more than inventory quantity"
                    throw InsufficientInventoryQuantityException(exceptionMessage)
                }
            }
        }.onFailure {
            throw InventoryServiceErrorException(it.message.toString())
        }

        return order
    }

    fun handleCreateOrderRetryFailure(
        orderRequestBody: OrderRequestBody,
        exception: InventoryServiceErrorException
    ): Order? {
        println("fallback")
        throw exception
    }

    private fun mapToOrder(orderRequestBody: OrderRequestBody): Order = Order(
        id = null,
        skuCode = orderRequestBody.skuCode,
        price = orderRequestBody.price,
        quantity = orderRequestBody.quantity
    )

    companion object {
        val mapper = ObjectMapper()
    }
}
