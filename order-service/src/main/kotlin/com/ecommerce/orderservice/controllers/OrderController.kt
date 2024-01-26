package com.ecommerce.orderservice.controllers

import com.ecommerce.orderservice.constants.MessageResponses
import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.dto.responses.Response
import com.ecommerce.orderservice.services.OrderService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {
    @PostMapping
    fun createOrder(
        @Valid @RequestBody orderRequestBody: OrderRequestBody
    ): ResponseEntity<Response> {
        val newOrder = orderService.createOrder(orderRequestBody)

        val message = MessageResponses.ORDER_CREATION_SUCCESS.message
        val response = Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = message,
            data = newOrder
        )

        logger.info("$message with id ${newOrder!!.id}")

        return ResponseEntity.ok(response)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}