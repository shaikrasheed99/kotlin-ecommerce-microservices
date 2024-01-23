package com.ecommerce.productservice.controllers

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.dto.requests.ProductRequestBody
import com.ecommerce.productservice.dto.response.Response
import com.ecommerce.productservice.services.ProductService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun createProduct(
        @Valid @RequestBody productRequestBody: ProductRequestBody
    ): ResponseEntity<Response> {
        val newProduct = productService.createProduct(productRequestBody)

        val message = MessageResponses.PRODUCT_CREATION_SUCCESS.message
        val response = Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = message,
            data = newProduct
        )

        logger.info("$message with id ${newProduct.id}")

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllProducts(): ResponseEntity<Response> {
        val products = productService.getAllProducts()

        val message = MessageResponses.PRODUCT_FETCHED_SUCCESS.message
        val response = Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = message,
            data = products
        )

        logger.info("$message of length ${products.size}")

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable("id") productId: Int
    ): ResponseEntity<Response> {
        val product = productService.getProductBy(productId)

        val message = MessageResponses.PRODUCT_FETCHED_SUCCESS.message
        val response = Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = message,
            data = product
        )

        logger.info("$message with id ${product.id}")

        return ResponseEntity.ok(response)
    }
}