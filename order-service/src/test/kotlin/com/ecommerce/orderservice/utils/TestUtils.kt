package com.ecommerce.orderservice.utils

import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.testcontainers.containers.PostgreSQLContainer
import java.math.BigDecimal

object TestUtils {
    fun getPostgreSQLContainer(): PostgreSQLContainer<*>? =
        PostgreSQLContainer("postgres:latest")
            .withDatabaseName("orders")
            .withInitScript("create-orders-table.sql")

    fun MockMvcResultMatchersDsl.assertCommonResponseBody(
        status: StatusResponses,
        code: HttpStatus,
        message: String
    ) {
        jsonPath("$.status") { value(status.name) }
        jsonPath("$.code") { value(code.name) }
        jsonPath("$.message") { value(message) }
    }

    fun createOrderRequestBodyJson(
        skuCode: String = "test_code",
        price: BigDecimal = BigDecimal(10),
        quantity: Int = 2
    ): String = ObjectMapper().writeValueAsString(
        OrderRequestBody(
            skuCode = skuCode,
            price = price,
            quantity = quantity
        )
    )
}
