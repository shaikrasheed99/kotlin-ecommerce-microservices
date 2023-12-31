package com.ecommerce.orderservice.services

import com.ecommerce.orderservice.configs.InventoryServiceClient
import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.dto.responses.InventoryResponse
import com.ecommerce.orderservice.dto.responses.Response
import com.ecommerce.orderservice.exceptions.InsufficientInventoryQuantityException
import com.ecommerce.orderservice.exceptions.InventoryNotAvailableException
import com.ecommerce.orderservice.exceptions.InventoryServiceErrorException
import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.models.OrderRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigDecimal
import java.net.URI
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class OrderServiceTest {
    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var inventoryServiceClient: InventoryServiceClient

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

        val inventorySuccessResponse = Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = "success response",
            data = InventoryResponse(
                id = 1,
                skuCode = "test_code",
                quantity = 2
            )
        )

        `when`(orderRepository.save(any(Order::class.java))).thenReturn(order)
        `when`(inventoryServiceClient.getInventoryBySkuCode(order.skuCode)).thenReturn(inventorySuccessResponse)

        val createdOrder = orderService.createOrder(orderRequestBody)

        createdOrder?.let {
            assertEquals(it.id, order.id)
            assertEquals(it.skuCode, order.skuCode)
            assertEquals(it.price, order.price)
            assertEquals(it.quantity, order.quantity)
        }

        verify(orderRepository, times(1)).save(any(Order::class.java))
        verify(inventoryServiceClient, times(1)).getInventoryBySkuCode(order.skuCode)
    }

    @Test
    internal fun shouldBeAbleToThrowInsufficientQuantityException() {
        val orderRequestBody = OrderRequestBody(
            skuCode = "test_code",
            price = BigDecimal(10.00),
            quantity = 1
        )

        val insufficientQuantityResponse = Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = "success response",
            data = InventoryResponse(
                id = 1,
                skuCode = "test_code",
                quantity = 0
            )
        )

        `when`(inventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)).thenReturn(insufficientQuantityResponse)

        assertThrows<InsufficientInventoryQuantityException> {
            orderService.createOrder(orderRequestBody)
        }
    }

    @Test
    internal fun shouldBeAbleToThrowInventoryNotAvailableException() {
        val orderRequestBody = OrderRequestBody(
            skuCode = "test_code",
            price = BigDecimal(10.00),
            quantity = 1
        )

        `when`(
            inventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
        ).thenThrow(
            WebClientResponseException.create(
                HttpStatus.NOT_FOUND.value(),
                "Not found",
                HttpHeaders.EMPTY,
                byteArrayOf(),
                null
            )
        )

        assertThrows<InventoryNotAvailableException> {
            orderService.createOrder(orderRequestBody)
        }
    }

    @Test
    internal fun shouldBeAbleToThrowInventoryServiceErrorException() {
        val orderRequestBody = OrderRequestBody(
            skuCode = "test_code",
            price = BigDecimal(10.00),
            quantity = 1
        )

        `when`(
            inventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
        ).thenThrow(
            WebClientRequestException(
                RuntimeException("test exception message"),
                HttpMethod.GET,
                URI.create("http://test.com"),
                HttpHeaders.EMPTY
            )
        )

        assertThrows<InventoryServiceErrorException> {
            orderService.createOrder(orderRequestBody)
        }
    }
}