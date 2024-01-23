package com.ecommerce.productservice.utils

import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.dto.requests.ProductRequestBody
import com.ecommerce.productservice.dto.response.Response
import com.ecommerce.productservice.models.Product
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import java.math.BigDecimal

object TestUtils {
    fun MockMvcResultMatchersDsl.assertCommonResponseBody(
        status: StatusResponses,
        code: HttpStatus,
        message: String
    ) {
        jsonPath("$.status") { value(status.name) }
        jsonPath("$.code") { value(code.name) }
        jsonPath("$.message") { value(message) }
    }

    fun createProduct(
        id: Int = 1,
        name: String = "test name",
        description: String = "test description",
        price: BigDecimal = BigDecimal(10.0)
    ) = Product(
        id = id,
        name = name,
        description = description,
        price = price
    )

    fun createProductRequestBody(
        name: String,
        description: String,
        price: BigDecimal
    ): ProductRequestBody =
        ProductRequestBody(
            name = name,
            description = description,
            price = price
        )

    fun createProductRequestBodyJson(
        name: String,
        description: String,
        price: BigDecimal
    ): String =
        createProductRequestBody(
            name = name,
            description = description,
            price = price
        ).let {
            ObjectMapper().writeValueAsString(it)
        }

    fun assertCommonFields(
        response: Response?,
        status: StatusResponses = StatusResponses.SUCCESS,
        code: HttpStatus = HttpStatus.OK,
        message: String,
        data: Any
    ) {
        response?.status shouldBe status
        response?.code shouldBe code
        response?.message shouldContain message
        response?.data shouldBe data
    }
}