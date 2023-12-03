package com.ecommerce.orderservice.controllers

import com.ecommerce.orderservice.constants.MessageResponses
import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.models.OrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
internal class OrderControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var orderRepository: OrderRepository

    companion object {
        @Container
        @ServiceConnection
        private val postgreSQLContainer = PostgreSQLContainer("postgres:latest")
            .withDatabaseName("orders")
            .withInitScript("create-orders-table.sql")
    }

    @AfterEach
    internal fun tearDown() {
        orderRepository.deleteAll()
    }

    @Test
    internal fun shouldBeAbleToCreateNewOrder() {
        val orderRequestBodyJson = OrderRequestBody(
            skuCode = "test_code",
            price = BigDecimal(10.00),
            quantity = 2
        ).let {
            ObjectMapper().writeValueAsString(it)
        }

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(StatusResponses.SUCCESS.name))
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.message").value(MessageResponses.ORDER_CREATION_SUCCESS.message))
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenSkuCodeIsBlank() {
        val orderRequestBodyJson = OrderRequestBody(
            skuCode = "",
            price = BigDecimal(10.00),
            quantity = 2
        ).let {
            ObjectMapper().writeValueAsString(it)
        }

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenPriceIsNegative() {
        val orderRequestBodyJson = OrderRequestBody(
            skuCode = "test_code",
            price = BigDecimal(-10.00),
            quantity = 2
        ).let {
            ObjectMapper().writeValueAsString(it)
        }

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenQuantityIsNegative() {
        val orderRequestBodyJson = OrderRequestBody(
            skuCode = "test_code",
            price = BigDecimal(10.00),
            quantity = -2
        ).let {
            ObjectMapper().writeValueAsString(it)
        }

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isBadRequest)
    }
}