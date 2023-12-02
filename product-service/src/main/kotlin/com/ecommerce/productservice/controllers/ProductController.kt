package com.ecommerce.productservice.controllers

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.dto.requests.ProductRequestBody
import com.ecommerce.productservice.dto.response.SuccessResponse
import com.ecommerce.productservice.services.ProductService
import com.ecommerce.productservice.utils.createSuccessResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun createProduct(@Valid @RequestBody productRequestBody: ProductRequestBody): ResponseEntity<SuccessResponse> {
        val newProduct = productService.createProduct(productRequestBody)

        val message = MessageResponses.PRODUCT_CREATION_SUCCESS.message
        val successResponse = createSuccessResponse(message, newProduct)

        logger.info("$message with id ${newProduct.id}")

        return ResponseEntity.ok(successResponse)
    }
}