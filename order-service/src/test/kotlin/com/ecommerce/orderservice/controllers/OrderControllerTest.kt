package com.ecommerce.orderservice.controllers

import com.ecommerce.orderservice.constants.MessageResponses
import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.dto.responses.InventoryResponse
import com.ecommerce.orderservice.dto.responses.Response
import com.ecommerce.orderservice.models.OrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType.APPLICATION_JSON
import org.mockserver.verify.VerificationTimes.exactly
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

        private val mockServerContainer = MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:5.15.0")
        ).apply { start() }

        private val mockServer = MockServerClient(
            mockServerContainer.host,
            mockServerContainer.serverPort
        )

        @JvmStatic
        @DynamicPropertySource
        private fun configure(registry: DynamicPropertyRegistry) {
            registry.add("inventory.service.url") {
                "http://${mockServerContainer.host}:${mockServerContainer.serverPort}"
            }
        }
    }

    @AfterEach
    internal fun tearDown() {
        orderRepository.deleteAll()
        mockServer.reset()
    }

    @Test
    internal fun shouldBeAbleToCreateNewOrder() {
        val orderRequestBodyJson = ObjectMapper().writeValueAsString(
            OrderRequestBody(
                skuCode = "test_code",
                price = BigDecimal(10.00),
                quantity = 2
            )
        )

        val inventorySuccessResponseJson = ObjectMapper().writeValueAsString(
            Response(
                status = StatusResponses.SUCCESS,
                code = HttpStatus.OK,
                message = "success response",
                data = InventoryResponse(
                    id = 1,
                    skuCode = "test_code",
                    quantity = 2
                )
            )
        )

        mockServer
            .`when`(
                request()
                    .withMethod(HttpMethod.GET.name())
                    .withPath("/inventory/.*")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(inventorySuccessResponseJson)
            )

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(StatusResponses.SUCCESS.name))
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.message").value(MessageResponses.ORDER_CREATION_SUCCESS.message))

        mockServer.verify(
            request().withMethod(HttpMethod.GET.name()).withPath("/inventory/.*"),
            exactly(1)
        );
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenSkuCodeIsBlank() {
        val orderRequestBodyJson = ObjectMapper().writeValueAsString(
            OrderRequestBody(
                skuCode = "",
                price = BigDecimal(10.00),
                quantity = 2
            )
        )

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenPriceIsNegative() {
        val orderRequestBodyJson = ObjectMapper().writeValueAsString(
            OrderRequestBody(
                skuCode = "test_code",
                price = BigDecimal(-10.00),
                quantity = 2
            )
        )

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenQuantityIsNegative() {
        val orderRequestBodyJson = ObjectMapper().writeValueAsString(
            OrderRequestBody(
                skuCode = "test_code",
                price = BigDecimal(10.00),
                quantity = -2
            )
        )

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenInsufficientInventoryQuantity() {
        val orderRequestBodyJson = ObjectMapper().writeValueAsString(
            OrderRequestBody(
                skuCode = "test_code",
                price = BigDecimal(10.00),
                quantity = 2
            )
        )

        val insufficientInventoryQuantityJson = ObjectMapper().writeValueAsString(
            Response(
                status = StatusResponses.SUCCESS,
                code = HttpStatus.OK,
                message = "success response",
                data = InventoryResponse(
                    id = 1,
                    skuCode = "test_code",
                    quantity = 1
                )
            )
        )

        mockServer
            .`when`(
                request()
                    .withMethod(HttpMethod.GET.name())
                    .withPath("/inventory/.*")
            ).respond(
                response()
                    .withStatusCode(HttpStatus.OK.value())
                    .withContentType(APPLICATION_JSON)
                    .withBody(insufficientInventoryQuantityJson)
            )

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isInternalServerError)

        mockServer.verify(
            request().withMethod(HttpMethod.GET.name()).withPath("/inventory/.*"),
            exactly(1)
        )
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenInventoryIsNotFoundInInventoryService() {
        val orderRequestBodyJson = ObjectMapper().writeValueAsString(
            OrderRequestBody(
                skuCode = "test_code",
                price = BigDecimal(10.00),
                quantity = 2
            )
        )

        val inventoryNotFoundResponseJson = ObjectMapper().writeValueAsString(
            Response(
                status = StatusResponses.ERROR,
                code = HttpStatus.NOT_FOUND,
                message = "inventory not found",
                data = null
            )
        )

        mockServer
            .`when`(
                request()
                    .withMethod(HttpMethod.GET.name())
                    .withPath("/inventory/.*")
            ).respond(
                response()
                    .withStatusCode(HttpStatus.NOT_FOUND.value())
                    .withContentType(APPLICATION_JSON)
                    .withBody(inventoryNotFoundResponseJson)
            )

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBodyJson)
        ).andExpect(status().isInternalServerError)

        mockServer.verify(
            request().withMethod(HttpMethod.GET.name()).withPath("/inventory/.*"),
            exactly(1)
        )
    }
}