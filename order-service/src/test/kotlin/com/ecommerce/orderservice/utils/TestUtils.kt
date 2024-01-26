package com.ecommerce.orderservice.utils

import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.dto.responses.Response
import com.ecommerce.orderservice.models.Order
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.testcontainers.containers.PostgreSQLContainer
import java.math.BigDecimal
import java.util.UUID

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

    fun createOrder(
        skuCode: String = "test_code",
        price: BigDecimal = BigDecimal(10),
        quantity: Int = 2
    ) = Order(
        id = UUID.randomUUID(),
        skuCode = skuCode,
        price = price,
        quantity = quantity
    )

    fun createOrderRequestBody(
        skuCode: String = "test_code",
        price: BigDecimal = BigDecimal(10),
        quantity: Int = 2
    ) = OrderRequestBody(
        skuCode = skuCode,
        price = price,
        quantity = quantity
    )

    fun createOrderRequestBodyJson(
        skuCode: String = "test_code",
        price: BigDecimal = BigDecimal(10),
        quantity: Int = 2
    ): String = ObjectMapper().writeValueAsString(
        createOrderRequestBody(
            skuCode = skuCode,
            price = price,
            quantity = quantity
        )
    )

    fun assertCommonFields(
        response: Response?,
        status: StatusResponses = StatusResponses.SUCCESS,
        code: HttpStatus = HttpStatus.OK,
        message: String,
        data: Any
    ) {
        response?.status shouldBe status
        response?.code shouldBe code
        response?.message shouldContain message
        response?.data shouldBe data
    }
}
