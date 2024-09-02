package com.ecommerce.orderservice.services

import com.ecommerce.orderservice.configs.InventoryServiceClient
import com.ecommerce.orderservice.constants.Events.ORDER_PLACED_EVENT_TYPE
import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.dto.responses.InventoryResponse
import com.ecommerce.orderservice.events.OrderPlacedEvent
import com.ecommerce.orderservice.exceptions.InsufficientInventoryQuantityException
import com.ecommerce.orderservice.exceptions.InventoryServiceErrorException
import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.models.OrderRepository
import com.ecommerce.orderservice.models.Outbox
import com.ecommerce.orderservice.models.OutboxRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val inventoryServiceClient: InventoryServiceClient,
    private val outboxRepository: OutboxRepository,
    @Value("\${spring.kafka.topic}")
    private val kafkaTopic: String
) {
    @CircuitBreaker(name = "inventoryClient")
    @Retry(name = "inventoryClient", fallbackMethod = "handleCreateOrderRetryFailure")
    @Transactional
    fun createOrder(orderRequestBody: OrderRequestBody): Order? {
        var order: Order? = null

        runCatching {
            inventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
        }.onSuccess { inventoryResponse ->
            inventoryResponse?.let {
                val inventory = mapper.convertValue(it.data, InventoryResponse::class.java)
                if (orderRequestBody.quantity <= inventory.quantity) {
                    order = orderRepository.save(mapToOrder(orderRequestBody))

                    val eventToJsonString = convertEventToJsonString(mapToOrderPlacedEvent(order))
                    outboxRepository.save(buildOutbox(eventToJsonString))
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

    private fun buildOutbox(eventPayload: String) = Outbox (
        eventId = UUID.randomUUID(),
        eventType = ORDER_PLACED_EVENT_TYPE,
        eventPayload = eventPayload,
        topic = kafkaTopic
    )

    private fun convertEventToJsonString(orderPlacedEvent: OrderPlacedEvent): String {
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(orderPlacedEvent)
    }

    private fun mapToOrderPlacedEvent(order: Order?): OrderPlacedEvent =
        OrderPlacedEvent(
            orderId = order!!.id!!,
            skuCode = order.skuCode,
            quantity = order.quantity
        )

    @Suppress("UnusedParameter")
    fun handleCreateOrderRetryFailure(
        orderRequestBody: OrderRequestBody,
        exception: InventoryServiceErrorException
    ): Order? {
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
