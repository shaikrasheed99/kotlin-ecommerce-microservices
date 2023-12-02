package com.ecommerce.productservice.controllers

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.dto.requests.ProductRequestBody
import com.ecommerce.productservice.models.Product
import com.ecommerce.productservice.models.ProductRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
internal class ProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var productRepository: ProductRepository

    companion object {
        @Container
        @ServiceConnection
        private val postgreSQLContainer = PostgreSQLContainer("postgres:latest")
            .withDatabaseName("products")
            .withInitScript("create-products-table.sql")
    }

    private lateinit var product: Product

    @BeforeEach
    internal fun setUp() {
        product = Product(
            id = 1,
            name = "test name",
            description = "test description",
            price = BigDecimal(10.20)
        ).also(productRepository::save)
    }

    @AfterEach
    internal fun tearDown() {
        productRepository.deleteAll()
    }

    @Test
    internal fun shouldBeAbleToCreateNewProduct() {
        val productRequestBodyJson = ProductRequestBody(
            name = "test name",
            description = "test description",
            price = BigDecimal(10.20)
        ).let {
            ObjectMapper().writeValueAsString(it)
        }

        mockMvc.perform(
            post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestBodyJson)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(StatusResponses.SUCCESS.name))
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.message").value(MessageResponses.PRODUCT_CREATION_SUCCESS.message))
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewProductWhenNameIsBlank() {
        val productRequestBodyJson = ProductRequestBody(
            name = "",
            description = "test description",
            price = BigDecimal(10.20)
        ).let {
            ObjectMapper().writeValueAsString(it)
        }

        mockMvc.perform(
            post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestBodyJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewProductWhenDescriptionIsBlank() {
        val productRequestBodyJson = ProductRequestBody(
            name = "test name",
            description = "",
            price = BigDecimal(10.20)
        ).let {
            ObjectMapper().writeValueAsString(it)
        }

        mockMvc.perform(
            post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestBodyJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewProductWhenPriceIsNegative() {
        val productRequestBodyJson = ProductRequestBody(
            name = "test name",
            description = "test description",
            price = BigDecimal(-10.20)
        ).let {
            ObjectMapper().writeValueAsString(it)
        }

        mockMvc.perform(
            post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestBodyJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    internal fun shouldBeAbleToReturnAllProducts() {
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(StatusResponses.SUCCESS.name))
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.message").value(MessageResponses.PRODUCT_FETCHED_SUCCESS.message))
    }

    @Test
    internal fun shouldBeAbleToReturnProductById() {
        mockMvc.perform(get("/products/{id}", product.id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(StatusResponses.SUCCESS.name))
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.message").value(MessageResponses.PRODUCT_FETCHED_SUCCESS.message))
    }

    @Test
    internal fun shouldBeAbleToReturnErrorWhenProductIsNotFoundById() {
        mockMvc.perform(get("/products/{id}", 100))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(StatusResponses.ERROR.name))
            .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.message").value("${MessageResponses.PRODUCT_NOT_FOUND} with id 100"))
    }
}