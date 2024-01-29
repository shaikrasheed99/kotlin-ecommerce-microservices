package com.ecommerce.orderservice.unittests.dto.requests

import com.ecommerce.orderservice.dto.requests.OrderRequestBody
import com.ecommerce.orderservice.utils.EntityUtils.getAttributeAnnotations
import com.ecommerce.orderservice.utils.TestUtils.createOrderRequestBody
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

class OrderRequestBodyTest : DescribeSpec({
    lateinit var orderRequestBody: OrderRequestBody

    beforeEach {
        orderRequestBody = createOrderRequestBody()
    }

    describe("Order Request Body - getters") {
        it("should be able to have all the attributes in the request body") {
            orderRequestBody.skuCode shouldBe "test_code"
            orderRequestBody.price shouldBe BigDecimal(10.0)
            orderRequestBody.quantity shouldBe 2
        }
    }

    describe("Product Request Body - setters") {
        it("should be able to change SkuCode attribute of the request body") {
            orderRequestBody.skuCode = "another skuCode"

            orderRequestBody.skuCode shouldBe "another skuCode"
        }

        it("should be able to change Price attribute of the request body") {
            orderRequestBody.price = BigDecimal(20)

            orderRequestBody.price shouldBe BigDecimal(20)
        }

        it("should be able to change Quantity attribute of the request body") {
            orderRequestBody.quantity = 10

            orderRequestBody.quantity shouldBe 10
        }
    }

    describe("Product Request Body - annotations") {
        it("should have NotBlank annotation with message to the SkuCode attribute of request body") {
            val annotations = orderRequestBody.getAttributeAnnotations("skuCode")
            val notBlankAnnotation = annotations.firstOrNull { it is NotBlank } as NotBlank

            notBlankAnnotation shouldNotBe null
            notBlankAnnotation.message shouldBe "Requested Sku Code should not be blank"
        }

        it("should have Positive annotation with message to the Price attribute of request body") {
            val annotations = orderRequestBody.getAttributeAnnotations("price")
            val positiveAnnotation = annotations.firstOrNull { it is Positive } as Positive

            positiveAnnotation shouldNotBe null
            positiveAnnotation.message shouldBe "Requested Price should not be negative"
        }

        it("should have Positive annotation with message to the Quantity attribute of request body") {
            val annotations = orderRequestBody.getAttributeAnnotations("quantity")
            val positiveAnnotation = annotations.firstOrNull { it is Positive } as Positive

            positiveAnnotation shouldNotBe null
            positiveAnnotation.message shouldBe "Requested Quantity should not be negative"
        }
    }
})
