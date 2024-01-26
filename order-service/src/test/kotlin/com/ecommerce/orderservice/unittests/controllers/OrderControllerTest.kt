package com.ecommerce.orderservice.unittests.controllers

import com.ecommerce.orderservice.constants.MessageResponses
import com.ecommerce.orderservice.controllers.OrderController
import com.ecommerce.orderservice.services.OrderService
import com.ecommerce.orderservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.orderservice.utils.EntityUtils.getMethodParameterAnnotations
import com.ecommerce.orderservice.utils.TestUtils.assertCommonFields
import com.ecommerce.orderservice.utils.TestUtils.createOrder
import com.ecommerce.orderservice.utils.TestUtils.createOrderRequestBody
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

class OrderControllerTest : DescribeSpec({
    val mockOrderService = mockk<OrderService>()
    val orderController = OrderController(mockOrderService)

    val order = createOrder()

    describe("Order Controller - annotations") {
        it("should have RestController & RequestMapping annotations to the Order Controller class") {
            val classAnnotations = orderController.javaClass.annotations
            val restControllerAnnotation = classAnnotations.firstOrNull { it is RestController } as RestController
            val requestMappingAnnotation = classAnnotations.firstOrNull { it is RequestMapping } as RequestMapping

            restControllerAnnotation shouldNotBe null

            requestMappingAnnotation shouldNotBe null
            requestMappingAnnotation.value.firstOrNull { it == "/orders" } shouldNotBe null
        }
    }

    describe("Create New Order") {
        it("should be able to Create New Order") {
            val orderRequestBody = createOrderRequestBody()

            every { mockOrderService.createOrder(orderRequestBody) } returns order

            val response = orderController.createOrder(orderRequestBody).body

            assertCommonFields(
                response = response,
                message = MessageResponses.ORDER_CREATION_SUCCESS.message,
                data = order
            )

            verify {
                mockOrderService.createOrder(orderRequestBody)
            }
        }
    }

    describe("Create New Order - Error scenarios") {
        it("should be able to throw exception when service throws it") {
            val orderRequestBody = createOrderRequestBody()
            val exception = Exception("exception from service")

            every { mockOrderService.createOrder(orderRequestBody) } throws exception

            shouldThrow<Exception> { orderController.createOrder(orderRequestBody) }

            verify {
                mockOrderService.createOrder(orderRequestBody)
            }
        }
    }

    describe("Create New Order - annotations") {
        it("should have PostMapping annotation to the createOrder method") {
            val annotations = orderController.getMethodAnnotations("createOrder")
            val postMappingAnnotation = annotations.firstOrNull { it is PostMapping } as PostMapping

            postMappingAnnotation shouldNotBe null
        }

        it("should have Valid & RequestBody annotations to parameter of the createOrder method") {
            val annotations = orderController.getMethodParameterAnnotations(
                "createOrder",
                "orderRequestBody"
            )
            val validAnnotation = annotations.firstOrNull { it is Valid } as Valid
            val requestBodyAnnotations = annotations.firstOrNull { it is RequestBody } as RequestBody

            validAnnotation shouldNotBe null
            requestBodyAnnotations shouldNotBe null
        }
    }

    describe("Order Controller - logger") {
        it("should initialize the logger of order controller") {
            OrderController.logger shouldNotBe null
        }
    }
})
