package com.ecommerce.orderservice.unittests.models

import com.ecommerce.orderservice.models.Order
import com.ecommerce.orderservice.utils.EntityUtils.getAttributeAnnotations
import com.ecommerce.orderservice.utils.TestUtils.createOrder
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

class OrderTest : DescribeSpec({
    lateinit var order: Order

    beforeEach {
        order = createOrder()
    }

    describe("Order - getters") {
        it("should be able to have all the attributes to the Order class") {
            order.id shouldBe UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10")
            order.skuCode shouldBe "test_code"
            order.price shouldBe BigDecimal(10)
            order.quantity shouldBe 2
        }
    }

    describe("Order - setters") {
        it("should be able to change Id attribute of the Order") {
            order.id = UUID.fromString("bbb8a937-0504-4468-823a-04ccd6964d10")

            order.id shouldBe UUID.fromString("bbb8a937-0504-4468-823a-04ccd6964d10")
        }

        it("should be able to change SkuCode attribute of the Order") {
            order.skuCode = "another skuCode"

            order.skuCode shouldBe "another skuCode"
        }

        it("should be able to change Price attribute of the Order") {
            order.price = BigDecimal(100)

            order.price shouldBe BigDecimal(100)
        }

        it("should be able to change Quantity attribute of the Order") {
            order.quantity = 20

            order.quantity shouldBe 20
        }
    }

    describe("Order - annotations") {
        it("should have Entity & Table annotations to Order class") {
            val classAnnotations = order.javaClass.annotations
            val entityAnnotation = classAnnotations.firstOrNull { it is Entity } as Entity
            val tableAnnotation = classAnnotations.firstOrNull { it is Table } as Table

            entityAnnotation shouldNotBe null

            tableAnnotation shouldNotBe null
            tableAnnotation.name shouldBe "orders"
        }

        it("should have Id & GeneratedValue annotations to the Id attribute of Product class") {
            val annotations = order.getAttributeAnnotations("id")
            val idAnnotation = annotations.firstOrNull { it is Id } as Id
            val generatedValueAnnotation = annotations.firstOrNull { it is GeneratedValue } as GeneratedValue

            idAnnotation shouldNotBe null

            generatedValueAnnotation shouldNotBe null
            generatedValueAnnotation.strategy shouldBe GenerationType.UUID
        }
    }
})