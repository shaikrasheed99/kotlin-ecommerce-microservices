package com.ecommerce.productservice.unittests.dto.requests

import com.ecommerce.productservice.utils.EntityUtils.getAttributeAnnotations
import com.ecommerce.productservice.utils.TestUtils.createProductRequestBody
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

class ProductRequestBodyTest : DescribeSpec({
    describe("Product Request Body - getters") {
        it("should be able to have all the attributes in the request body") {
            val productRequestBody = createProductRequestBody()

            productRequestBody.name shouldBe "test name"
            productRequestBody.description shouldBe "test description"
            productRequestBody.price shouldBe BigDecimal(10.0)
        }
    }

    describe("Product Request Body - setters") {
        it("should be able to change name attribute of the request body") {
            val productRequestBody = createProductRequestBody()
            productRequestBody.name = "another name"

            productRequestBody.name shouldBe "another name"
        }

        it("should be able to change description attribute of the request body") {
            val productRequestBody = createProductRequestBody()
            productRequestBody.description = "another description"

            productRequestBody.description shouldBe "another description"
        }

        it("should be able to change price attribute of the request body") {
            val productRequestBody = createProductRequestBody()
            productRequestBody.price = BigDecimal(20)

            productRequestBody.price shouldBe BigDecimal(20)
        }
    }

    describe("Product Request Body - annotations") {
        it("should have NotBlank annotation with message to the Name attribute of request body class") {
            val productRequestBody = createProductRequestBody()
            val annotations = productRequestBody.getAttributeAnnotations("name")
            val notBlankAnnotation = annotations.firstOrNull { it is NotBlank } as NotBlank

            notBlankAnnotation shouldNotBe null
            notBlankAnnotation.message shouldBe "Request Product name should not be blank"
        }

        it("should have NotBlank annotation with message to the Description attribute of request body class") {
            val productRequestBody = createProductRequestBody()
            val annotations = productRequestBody.getAttributeAnnotations("description")
            val notBlankAnnotation = annotations.firstOrNull { it is NotBlank } as NotBlank

            notBlankAnnotation shouldNotBe null
            notBlankAnnotation.message shouldBe "Request Product description should not be blank"
        }

        it("should have Positive annotation with message to the Price attribute of request body class") {
            val productRequestBody = createProductRequestBody()
            val annotations = productRequestBody.getAttributeAnnotations("price")
            val positiveAnnotation = annotations.firstOrNull { it is Positive } as Positive

            positiveAnnotation shouldNotBe null
            positiveAnnotation.message shouldBe "Request Product price should be greater than zero"
        }
    }
})