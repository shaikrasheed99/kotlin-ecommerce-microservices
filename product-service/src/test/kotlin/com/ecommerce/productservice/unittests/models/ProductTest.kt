package com.ecommerce.productservice.unittests.models

import com.ecommerce.productservice.models.Product
import com.ecommerce.productservice.utils.TestUtils.createProduct
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class ProductTest : DescribeSpec({
    lateinit var product: Product

    beforeEach {
        product = createProduct()
    }

    describe("Product - getters") {
        it("should be able to have all the attributes in the product") {
            product.id shouldBe 1
            product.name shouldBe "test name"
            product.description shouldBe "test description"
            product.price shouldBe BigDecimal(10.0)
        }
    }

    describe("Product - setters") {
        it("should be able to change Id attribute of the product") {
            product.id = 100

            product.id shouldBe 100
        }

        it("should be able to change Name attribute of the product") {
            product.name = "another name"

            product.name shouldBe "another name"
        }

        it("should be able to change Description attribute of the product") {
            product.description = "another description"

            product.description shouldBe "another description"
        }

        it("should be able to change Price attribute of the product") {
            product.price = BigDecimal(100)

            product.price shouldBe BigDecimal(100)
        }
    }
})