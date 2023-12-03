package com.ecommerce.orderservice.services

import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.models.OrderRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class OrderServiceTest {
    @Mock
    private lateinit var orderRepository: OrderRepository

    @InjectMocks
    private lateinit var orderService: OrderService

    @Test
    internal fun shouldBeAbleToCreateNewOrder() {
        val orderRequestBody = OrderRequestBody(
            skuCode = "test_code",
            price = BigDecimal(10.00),
            quantity = 1
        )

        val order = Order(
            id = UUID.randomUUID(),
            skuCode = orderRequestBody.skuCode,
            price = orderRequestBody.price,
            quantity = orderRequestBody.quantity
        )

        `when`(orderRepository.save(any(Order::class.java))).thenReturn(order)

        assertDoesNotThrow { orderService.createOrder(orderRequestBody) }

        verify(orderRepository, times(1)).save(any(Order::class.java))
    }
}