package com.ecommerce.orderservice.services

import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.models.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRepository: OrderRepository) {
    fun createOrder(orderRequestBody: OrderRequestBody): Order {
        return Order(
            id = null,
            skuCode = orderRequestBody.skuCode,
            price = orderRequestBody.price,
            quantity = orderRequestBody.quantity
        ).also {
            orderRepository.save(it)
        }
    }
}
