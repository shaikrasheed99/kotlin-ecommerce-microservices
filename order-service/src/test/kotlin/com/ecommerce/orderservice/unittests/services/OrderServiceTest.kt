package com.ecommerce.orderservice.unittests.services

import com.ecommerce.orderservice.configs.InventoryServiceClient
import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.responses.InventoryResponse
import com.ecommerce.orderservice.dto.responses.Response
import com.ecommerce.orderservice.events.OrderPlacedEvent
import com.ecommerce.orderservice.exceptions.InsufficientInventoryQuantityException
import com.ecommerce.orderservice.exceptions.InventoryServiceErrorException
import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.models.OrderRepository
import com.ecommerce.orderservice.producer.EventProducer
import com.ecommerce.orderservice.services.OrderService
import com.ecommerce.orderservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.orderservice.utils.TestUtils.createOrder
import com.ecommerce.orderservice.utils.TestUtils.createOrderRequestBody
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientRequestException
import java.net.URI

private const val CIRCUIT_BREAKER_INVENTORY_CLIENT = "inventoryClient"

internal class OrderServiceTest : DescribeSpec({
    val mockOrderRepository = mockk<OrderRepository>()
    val mockInventoryServiceClient = mockk<InventoryServiceClient>()
    val mockEventProducer = mockk<EventProducer>()
    val kafkaTopic = "testTopic"

    val orderService = OrderService(
        mockOrderRepository,
        mockInventoryServiceClient,
        mockEventProducer,
        kafkaTopic
    )

    val order = createOrder()

    describe("Order Service - annotations") {
        it("should have Service annotation to the Order Service class") {
            val classAnnotations = orderService.javaClass.annotations
            val serviceAnnotation = classAnnotations.firstOrNull { it is Service } as Service

            serviceAnnotation shouldNotBe null
        }
    }

    describe("Create New Order") {
        it("should be able to create New Order") {
            val orderRequestBody = createOrderRequestBody()

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

            every { mockOrderRepository.save(any(Order::class)) } returns order
            every {
                mockEventProducer.sendOrderPlacedEvent(any(String::class), any(OrderPlacedEvent::class))
            } just runs
            every {
                mockInventoryServiceClient.getInventoryBySkuCode(order.skuCode)
            } returns inventorySuccessResponse

            val createdOrder = orderService.createOrder(orderRequestBody)

            createdOrder?.id shouldBe order.id
            createdOrder?.skuCode shouldBe order.skuCode
            createdOrder?.price shouldBe order.price
            createdOrder?.quantity shouldBe order.quantity

            verify {
                mockOrderRepository.save(any(Order::class))
                mockEventProducer.sendOrderPlacedEvent(any(String::class), any(OrderPlacedEvent::class))
                mockInventoryServiceClient.getInventoryBySkuCode(order.skuCode)
            }
        }
    }

    describe("Create New Order - Error scenarios") {
        it("should be able to throw InsufficientQuantityException when quantity is less in inventory service") {
            val orderRequestBody = createOrderRequestBody()

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

            every {
                mockInventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
            } returns insufficientQuantityResponse

            shouldThrow<InsufficientInventoryQuantityException> {
                orderService.createOrder(orderRequestBody)
            }

            verify {
                mockInventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
            }
        }

        it("should be able to throw InventoryServiceErrorException when inventory is not present by skuCode") {
            val orderRequestBody = createOrderRequestBody()

            val webClientRequestException = WebClientRequestException(
                RuntimeException("test exception message"),
                HttpMethod.GET,
                URI.create("http://test.com"),
                HttpHeaders.EMPTY
            )

            every {
                mockInventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
            } throws webClientRequestException

            shouldThrow<InventoryServiceErrorException> {
                orderService.createOrder(orderRequestBody)
            }

            verify {
                mockInventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
            }
        }

        it("should return order as null when the response from inventory service is null") {
            val orderRequestBody = createOrderRequestBody()

            every {
                mockInventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
            } returns null

            val createdOrder = orderService.createOrder(orderRequestBody)

            createdOrder shouldBe null

            verify {
                mockInventoryServiceClient.getInventoryBySkuCode(orderRequestBody.skuCode)
            }
        }
    }

    describe("Create New Order - Circuit Breaker & Retry - annotations") {
        it("should have Circuit Breaker annotation to the createOrder method") {
            val methodAnnotations = orderService.getMethodAnnotations("createOrder")
            val circuitBreakerAnnotation = methodAnnotations.firstOrNull { it is CircuitBreaker } as CircuitBreaker

            circuitBreakerAnnotation shouldNotBe null
            circuitBreakerAnnotation.name shouldBe CIRCUIT_BREAKER_INVENTORY_CLIENT
        }

        it("should have Retry annotation to the createOrder method") {
            val methodAnnotations = orderService.getMethodAnnotations("createOrder")
            val retryAnnotation = methodAnnotations.firstOrNull { it is Retry } as Retry

            retryAnnotation shouldNotBe null
            retryAnnotation.name shouldBe CIRCUIT_BREAKER_INVENTORY_CLIENT
            retryAnnotation.fallbackMethod shouldBe "handleCreateOrderRetryFailure"
        }
    }

    describe("Order Service - mapper") {
        it("should initialize the mapper of order service") {
            OrderService.mapper shouldNotBe null
        }
    }
})
