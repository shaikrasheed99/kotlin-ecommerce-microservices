package com.ecommerce.productservice.unittests.services

import com.ecommerce.productservice.exceptions.ProductNotFound
import com.ecommerce.productservice.models.Product
import com.ecommerce.productservice.models.ProductRepository
import com.ecommerce.productservice.services.ProductService
import com.ecommerce.productservice.utils.TestUtils.createProduct
import com.ecommerce.productservice.utils.TestUtils.createProductRequestBody
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.stereotype.Service
import java.util.Optional

class ProductServiceTest : DescribeSpec({
    val productRepository = mockk<ProductRepository>()
    val productService = ProductService(productRepository)

    val product = createProduct()

    describe("Product Service - annotations") {
        it("should have Service annotation to the Product Service class") {
            val classAnnotations = productService.javaClass.annotations
            val serviceAnnotation = classAnnotations.firstOrNull { it is Service } as Service

            serviceAnnotation shouldNotBe null
        }
    }

    describe("Create New Product") {
        it("should be able to create New Product") {
            val productRequestBody = createProductRequestBody()

            every { productRepository.save(any(Product::class)) } returns product

            val createdProduct = productService.createProduct(productRequestBody)

            createdProduct shouldBe product
            verify { productRepository.save(any(Product::class)) }
        }

        describe("Error scenarios") {
            it("should throw exception when the exception thrown from repository layer") {
                val productRequestBody = createProductRequestBody()

                val exception = Exception("exception from the repository")
                every { productRepository.save(any(Product::class)) } throws exception

                shouldThrow<Exception> { productService.createProduct(productRequestBody) }

                verify { productRepository.save(any(Product::class)) }
            }
        }
    }

    describe("Get List of Products") {
        it("should be able to return list of products") {
            val products = listOf(product)
            every { productRepository.findAll() } returns products

            val listOfProducts = productService.getAllProducts()

            listOfProducts shouldBe products
            verify { productRepository.findAll() }
        }
    }

    describe("Get Product by Id") {
        it("should be able to return product by id") {
            every { productRepository.findById(product.id!!) } returns Optional.ofNullable(product)

            val productById = productService.getProductBy(product.id!!)

            productById shouldBe product
            verify { productRepository.findById(product.id!!) }
        }

        describe("Error scenarios") {
            it("should throw exception when the Product is not found by id") {
                val productId = 1
                every { productRepository.findById(productId) } returns Optional.empty()

                shouldThrow<ProductNotFound> { productService.getProductBy(productId) }

                verify { productRepository.findById(productId) }
            }
        }
    }
})
