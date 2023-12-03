package com.ecommerce.orderservice.controllers

import com.ecommerce.orderservice.constants.MessageResponses
import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.dto.responses.SuccessResponse
import com.ecommerce.orderservice.services.OrderService
import com.ecommerce.orderservice.utils.createSuccessResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun createOrder(
        @RequestBody orderRequestBody: OrderRequestBody
    ): ResponseEntity<SuccessResponse> {
        val newOrder = orderService.createOrder(orderRequestBody)

        val message = MessageResponses.ORDER_CREATION_SUCCESS.message
        val successResponse = createSuccessResponse(message, newOrder)

        logger.info("$message with id ${newOrder.id}")

        return ResponseEntity.ok(successResponse)
    }
}