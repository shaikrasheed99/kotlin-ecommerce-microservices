package com.ecommerce.orderservice.integrationtests.controllers

import com.ecommerce.orderservice.constants.Events
import com.ecommerce.orderservice.constants.MessageResponses
import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.models.OrderRepository
import com.ecommerce.orderservice.models.OutboxRepository
import com.ecommerce.orderservice.utils.InventoryServiceMockServerStubUtils.invokeGetInventoryBySkuCodeAPIResponse200
import com.ecommerce.orderservice.utils.InventoryServiceMockServerStubUtils.invokeGetInventoryBySkuCodeAPIResponse404
import com.ecommerce.orderservice.utils.InventoryServiceMockServerStubUtils.invokeGetInventoryBySkuCodeWithInsufficientQuantityAPIResponse200
import com.ecommerce.orderservice.utils.InventoryServiceMockServerStubUtils.invokeInventoryServiceNotAvailableAPIResponse
import com.ecommerce.orderservice.utils.InventoryServiceMockServerStubUtils.verifyGetInventoryBySkuCodeAPICall
import com.ecommerce.orderservice.utils.TestUtils.assertCommonResponseBody
import com.ecommerce.orderservice.utils.TestUtils.createOrderRequestBodyJson
import com.ecommerce.orderservice.utils.TestUtils.getPostgreSQLContainer
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal

private const val NUMBER_OF_RETRY_CALLS = 3

const val DEFAULT_TEST_TOPIC = "testTopic"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
internal class OrderControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var outboxRepository: OutboxRepository

    @Autowired
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry

    companion object {
        @Container
        @ServiceConnection
        val postgreSQLContainer = getPostgreSQLContainer()

        private val mockServerContainer = MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:latest")
        ).apply { start() }

        private val mockServer = MockServerClient(
            mockServerContainer.host,
            mockServerContainer.serverPort
        )

        @JvmStatic
        @DynamicPropertySource
        fun configure(registry: DynamicPropertyRegistry) {
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
        val orderRequestBodyJson = createOrderRequestBodyJson()

        invokeGetInventoryBySkuCodeAPIResponse200(mockServer)

        val result = mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = orderRequestBodyJson
        }.andExpect {
            status { isOk() }
            assertCommonResponseBody(
                status = StatusResponses.SUCCESS,
                code = HttpStatus.OK,
                message = MessageResponses.ORDER_CREATION_SUCCESS.message
            )
        }.andReturn()

        val responseContent = result.response.contentAsString
        val objectMapper = ObjectMapper()
        val orderDataNode = objectMapper.readTree(responseContent)["data"]
        val createdOrder = objectMapper.treeToValue(orderDataNode, Order::class.java)

        orderRepository.count() shouldBe 1
        createdOrder.id shouldNotBe null
        createdOrder.skuCode shouldBe "test_code"
        createdOrder.price shouldBe BigDecimal(10)
        createdOrder.quantity shouldBe 2

        outboxRepository.count() shouldBe 1
        val actualOutbox = outboxRepository.findAll()[0]
        actualOutbox.eventId shouldNotBe null
        actualOutbox.eventType shouldBe Events.ORDER_PLACED_EVENT_TYPE
        actualOutbox.topic shouldBe "testTopic"

        val objectMapperTwo = ObjectMapper()
        val actualPayloadNode = objectMapperTwo.readTree(actualOutbox.eventPayload)
        val actualPayload = objectMapperTwo.treeToValue(actualPayloadNode, Map::class.java)

        actualPayload["skuCode"] shouldBe createdOrder.skuCode
        actualPayload["quantity"] shouldBe createdOrder.quantity
        verifyGetInventoryBySkuCodeAPICall(mockServer, 1)
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenSkuCodeIsBlank() {
        val orderRequestBodyJson = createOrderRequestBodyJson(
            skuCode = ""
        )

        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = orderRequestBodyJson
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenPriceIsNegative() {
        val orderRequestBodyJson = createOrderRequestBodyJson(
            price = BigDecimal(-10.00)
        )

        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = orderRequestBodyJson
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenQuantityIsNegative() {
        val orderRequestBodyJson = createOrderRequestBodyJson(
            quantity = -2
        )

        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = orderRequestBodyJson
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenInsufficientInventoryQuantity() {
        val orderRequestBodyJson = createOrderRequestBodyJson()

        invokeGetInventoryBySkuCodeWithInsufficientQuantityAPIResponse200(mockServer)

        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = orderRequestBodyJson
        }.andExpect {
            status { isInternalServerError() }
        }

        orderRepository.count() shouldBe 0
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewOrderWhenInventoryIsNotFoundInInventoryService() {
        val orderRequestBodyJson = createOrderRequestBodyJson()

        invokeGetInventoryBySkuCodeAPIResponse404(mockServer)

        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = orderRequestBodyJson
        }.andExpect {
            status { isInternalServerError() }
        }

        orderRepository.count() shouldBe 0
    }

    @Test
    internal fun shouldBeAbleToRetryTheInventoryAPICallWhenInventoryIsNotFound() {
        val orderRequestBodyJson = createOrderRequestBodyJson()

        invokeGetInventoryBySkuCodeAPIResponse404(mockServer)

        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = orderRequestBodyJson
        }.andExpect {
            status { isInternalServerError() }
        }

        verifyGetInventoryBySkuCodeAPICall(mockServer, NUMBER_OF_RETRY_CALLS)
    }

    @Test
    internal fun shouldBeAbleToRetryTheInventoryAPICallWhenInventoryServiceIsNotAvailable() {
        val orderRequestBodyJson = createOrderRequestBodyJson()

        invokeInventoryServiceNotAvailableAPIResponse(mockServer)

        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = orderRequestBodyJson
        }.andExpect {
            status { isInternalServerError() }
        }

        verifyGetInventoryBySkuCodeAPICall(mockServer, NUMBER_OF_RETRY_CALLS)
    }

    @Test
    internal fun shouldHaveClosedStateInCircuitBreakerWhenInventoryServiceReturnsSuccess() {
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("inventoryClient")
        circuitBreaker.state shouldBe CircuitBreaker.State.CLOSED

        val orderRequestBodyJson = createOrderRequestBodyJson()

        invokeGetInventoryBySkuCodeAPIResponse200(mockServer)

        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = orderRequestBodyJson
        }.andExpect {
            status { isOk() }
        }

        verifyGetInventoryBySkuCodeAPICall(mockServer, 1)

        circuitBreaker.state shouldBe CircuitBreaker.State.CLOSED
    }
}
