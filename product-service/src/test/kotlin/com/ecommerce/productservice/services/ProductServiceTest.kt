package com.ecommerce.productservice.services

import com.ecommerce.productservice.dto.requests.ProductRequestBody
import com.ecommerce.productservice.exceptions.ProductNotFound
import com.ecommerce.productservice.models.Product
import com.ecommerce.productservice.models.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.util.Optional

class ProductServiceTest : DescribeSpec({
    describe("Product Service Tests") {
        val productRepository = mockk<ProductRepository>()
        val productService = ProductService(productRepository)

        describe("Create New Product") {
            it("should be able to create New Product") {
                val productRequestBody = ProductRequestBody(
                    name = "test name",
                    description = "test description",
                    price = BigDecimal(10.20)
                )
                val product = Product(
                    id = 1,
                    name = "test name",
                    description = "test description",
                    price = BigDecimal(10.20)
                )
                every { productRepository.save(any(Product::class)) } returns product

                val createdProduct = productService.createProduct(productRequestBody)

                createdProduct shouldBe product
                verify { productRepository.save(any(Product::class)) }
            }

            describe("Error scenarios") {
                it("should throw exception when the exception thrown from repository layer") {
                    val productRequestBody = ProductRequestBody(
                        name = "test name",
                        description = "test description",
                        price = BigDecimal(10.20)
                    )
                    val exception = Exception("exception from the repository")
                    every { productRepository.save(any(Product::class)) } throws exception

                    shouldThrow<Exception> { productService.createProduct(productRequestBody) }

                    verify { productRepository.save(any(Product::class)) }
                }
            }
        }

        describe("Get List of Products") {
            it("should be able to return list of products") {
                val products = listOf<Product>(
                    Product(
                        id = 1,
                        name = "test name",
                        description = "test description",
                        price = BigDecimal(10.20)
                    )
                )
                every { productRepository.findAll() } returns products

                val listOfProducts = productService.getAllProducts()

                listOfProducts shouldBe products
                verify { productRepository.findAll() }
            }
        }

        describe("Get Product by Id") {
            it("should be able to return product by id") {
                val product = Product(
                    id = 1,
                    name = "test name",
                    description = "test description",
                    price = BigDecimal(10.20)
                )
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
    }
})