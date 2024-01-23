package com.ecommerce.productservice.integrationtests

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.models.Product
import com.ecommerce.productservice.models.ProductRepository
import com.ecommerce.productservice.utils.TestUtils.assertCommonResponseBody
import com.ecommerce.productservice.utils.TestUtils.createProduct
import com.ecommerce.productservice.utils.TestUtils.createProductRequestBodyJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
internal class ProductControllerIntegrationTest(
    private var mockMvc: MockMvc,
    @Autowired private var productRepository: ProductRepository
) : DescribeSpec({
    val product = createProduct()

    afterEach {
        productRepository.deleteAll()
    }

    describe("Create New Product") {
        it("should be able to Create New Product") {
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
    }

    describe("Create New Product - Error scenarios") {
        it("should not be able to Create New Product when Name is blank") {
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

        it("should not be able to Create New Product when Description is blank") {
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

        it("should not be able to Create New Product when Price is negative") {
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
    }

    describe("Get all Products") {
        it("should be able to return list of Products") {
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

        it("should be able to return empty list when Products are not present in database") {
            mockMvc.get("/products")
                .andExpect {
                    status { isOk() }
                    assertCommonResponseBody(
                        status = StatusResponses.SUCCESS,
                        code = HttpStatus.OK,
                        message = MessageResponses.PRODUCT_FETCHED_SUCCESS.message
                    )
                }

            productRepository.count() shouldBe 0
        }
    }

    describe("Get Product by Id - Error scenarios") {
        it("should be able to return Error when Product is not found by Id") {
            mockMvc.get("/products/{id}", product.id)
                .andExpect {
                    status { isNotFound() }
                    assertCommonResponseBody(
                        status = StatusResponses.ERROR,
                        code = HttpStatus.NOT_FOUND,
                        message = "${MessageResponses.PRODUCT_NOT_FOUND.message} with id ${product.id}"
                    )
                }

            productRepository.count() shouldBe 0
        }
    }
})

private fun MockMvcResultMatchersDsl.assertProductDetailsBody(product: Product) {
    jsonPath("$.data.name") { value(product.name) }
    jsonPath("$.data.description") { value(product.description) }
}
