package com.ecommerce.productservice.integrationtests

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.models.Product
import com.ecommerce.productservice.models.ProductRepository
import com.ecommerce.productservice.utils.TestUtils.assertCommonResponseBody
import com.ecommerce.productservice.utils.TestUtils.createProduct
import com.ecommerce.productservice.utils.TestUtils.createProductRequestBodyJson
import com.ecommerce.productservice.utils.TestUtils.getPostgreSQLContainer
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
internal class ProductControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var productRepository: ProductRepository

    companion object {
        @Container
        @ServiceConnection
        val postgreSQLContainer = getPostgreSQLContainer()
    }

    val product = createProduct()

    @AfterEach
    internal fun tearDown() {
        productRepository.deleteAll()
    }

    @Test
    internal fun shouldBeAbleToCreateNewProduct() {
        val productRequestBodyJson = createProductRequestBodyJson(
            name = product.name,
            description = product.description,
            price = product.price
        )

        mockMvc.post("/products") {
            contentType = MediaType.APPLICATION_JSON
            content = productRequestBodyJson
        }.andExpect {
            status { isOk() }
            assertCommonResponseBody(
                status = StatusResponses.SUCCESS,
                code = HttpStatus.OK,
                message = MessageResponses.PRODUCT_CREATION_SUCCESS.message
            )
            assertProductDetailsBody(product)
        }

        productRepository.count() shouldBe 1
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewProductWhenNameIsBlank() {
        val productRequestBodyJson = createProductRequestBodyJson(
            name = "",
            description = product.description,
            price = product.price
        )

        mockMvc.post("/products") {
            contentType = MediaType.APPLICATION_JSON
            content = productRequestBodyJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.name") {
                value("Request Product name should not be blank")
            }
        }

        productRepository.count() shouldBe 0
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewProductWhenDescriptionIsBlank() {
        val productRequestBodyJson = createProductRequestBodyJson(
            name = product.name,
            description = "",
            price = product.price
        )

        mockMvc.post("/products") {
            contentType = MediaType.APPLICATION_JSON
            content = productRequestBodyJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.description") {
                value("Request Product description should not be blank")
            }
        }

        productRepository.count() shouldBe 0
    }

    @Test
    internal fun shouldNotBeAbleToCreateNewProductWhenPriceIsNegative() {
        val productRequestBodyJson = createProductRequestBodyJson(
            name = product.name,
            description = "",
            price = BigDecimal(-10.20)
        )

        mockMvc.post("/products") {
            contentType = MediaType.APPLICATION_JSON
            content = productRequestBodyJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.price") {
                value("Request Product price should be greater than zero")
            }
        }

        productRepository.count() shouldBe 0
    }

    @Test
    internal fun shouldBeAbleToReturnAllProducts() {
        productRepository.save(product)

        mockMvc.get("/products")
            .andExpect {
                status { isOk() }
                assertCommonResponseBody(
                    status = StatusResponses.SUCCESS,
                    code = HttpStatus.OK,
                    message = MessageResponses.PRODUCT_FETCHED_SUCCESS.message
                )
                jsonPath("$.data[0].name") { value(product.name) }
                jsonPath("$.data[0].description") { value(product.description) }
            }

        productRepository.count() shouldBe 1
    }

    @Test
    internal fun shouldBeAbleToReturnProductById() {
        productRepository.save(product)

        mockMvc.get("/products/{id}", product.id)
            .andExpect {
                status { isOk() }
                assertCommonResponseBody(
                    status = StatusResponses.SUCCESS,
                    code = HttpStatus.OK,
                    message = MessageResponses.PRODUCT_FETCHED_SUCCESS.message
                )
            }

        productRepository.count() shouldBe 1
    }

    @Test
    internal fun shouldBeAbleToReturnErrorWhenProductIsNotFoundById() {
        productRepository.deleteAll()

        mockMvc.get("/products/{id}", 100)
            .andExpect {
                status { isNotFound() }
                assertCommonResponseBody(
                    status = StatusResponses.ERROR,
                    code = HttpStatus.NOT_FOUND,
                    message = "${MessageResponses.PRODUCT_NOT_FOUND.message} with id 100"
                )
            }

        productRepository.count() shouldBe 0
    }
}

private fun MockMvcResultMatchersDsl.assertProductDetailsBody(product: Product) {
    jsonPath("$.data.name") { value(product.name) }
    jsonPath("$.data.description") { value(product.description) }
}
