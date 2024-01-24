package com.ecommerce.productservice.unittests.controllers

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.controllers.ProductController
import com.ecommerce.productservice.exceptions.ProductNotFound
import com.ecommerce.productservice.services.ProductService
import com.ecommerce.productservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.productservice.utils.TestUtils.assertCommonFields
import com.ecommerce.productservice.utils.TestUtils.createProduct
import com.ecommerce.productservice.utils.TestUtils.createProductRequestBody
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

class ProductControllerTest : DescribeSpec({
    val mockProductService = mockk<ProductService>()
    val productController = ProductController(mockProductService)

    val product = createProduct()

    describe("Product Controller - annotations") {
        it("should have relevant annotations to the Product Controller class") {
            val classAnnotations = productController.javaClass.annotations
            val restControllerAnnotation = classAnnotations.firstOrNull { it is RestController } as RestController
            val requestMappingAnnotation = classAnnotations.firstOrNull { it is RequestMapping } as RequestMapping

            restControllerAnnotation shouldNotBe null

            requestMappingAnnotation shouldNotBe null
            requestMappingAnnotation.value.firstOrNull { it == "/products" } shouldNotBe null
        }
    }

    describe("Create New Product") {
        it("should be able to Create New Product") {
            val productRequestBody = createProductRequestBody(
                name = product.name,
                description = product.description,
                price = product.price
            )
            every { mockProductService.createProduct(productRequestBody) } returns product

            val response = productController.createProduct(productRequestBody).body

            assertCommonFields(
                response = response,
                message = MessageResponses.PRODUCT_CREATION_SUCCESS.message,
                data = product
            )

            verify {
                mockProductService.createProduct(productRequestBody)
            }
        }
    }

    describe("Create New Product - annotations") {
        it("should have PostMapping annotation to the createProduct method") {
            val annotations = productController.getMethodAnnotations("createProduct")
            val postMappingAnnotation = annotations.firstOrNull { it is PostMapping } as PostMapping

            postMappingAnnotation shouldNotBe null
        }
    }

    describe("Get all Products") {
        it("should be able to return list of Products") {
            every { mockProductService.getAllProducts() } returns mutableListOf(product)

            val response = productController.getAllProducts().body

            assertCommonFields(
                response = response,
                message = MessageResponses.PRODUCT_FETCHED_SUCCESS.message,
                data = mutableListOf(product)
            )

            verify {
                mockProductService.getAllProducts()
            }
        }

        it("should be able to return empty list when Products are not present in database") {
            every { mockProductService.getAllProducts() } returns mutableListOf()

            val response = productController.getAllProducts().body

            assertCommonFields(
                response = response,
                message = MessageResponses.PRODUCT_FETCHED_SUCCESS.message,
                data = intArrayOf()
            )

            verify {
                mockProductService.getAllProducts()
            }
        }
    }

    describe("Get all Products - Error scenarios") {
        it("should be able to throw Exception when service throws exception") {
            val exception = Exception("some exception from service")
            every { mockProductService.getAllProducts() } throws exception

            shouldThrow<Exception> { productController.getAllProducts() }

            verify {
                mockProductService.getAllProducts()
            }
        }
    }

    describe("Get all Products - annotations") {
        it("should have GetMapping annotation to the getAllProducts method") {
            val annotations = productController.getMethodAnnotations("getAllProducts")
            val getMappingAnnotation = annotations.firstOrNull { it is GetMapping } as GetMapping

            getMappingAnnotation shouldNotBe null
        }
    }

    describe("Get Product by Id") {
        it("should be able to return Product by Id") {
            every { mockProductService.getProductBy(product.id!!) } returns product

            val response = productController.getProductById(product.id!!).body

            assertCommonFields(
                response = response,
                message = MessageResponses.PRODUCT_FETCHED_SUCCESS.message,
                data = product
            )

            verify {
                mockProductService.getProductBy(product.id!!)
            }
        }
    }

    describe("Get Product by Id - Error scenarios") {
        it("should be able to throw Product Not Found when Product is not found by Id") {
            val productNotFoundException = ProductNotFound("product not found")
            every { mockProductService.getProductBy(product.id!!) } throws productNotFoundException

            shouldThrow<ProductNotFound> { productController.getProductById(product.id!!) }

            verify {
                mockProductService.getProductBy(product.id!!)
            }
        }
    }

    describe("Get Product by Id - annotations") {
        it("should have GetMapping annotation with path value to the getProductById method") {
            val annotations = productController.getMethodAnnotations("getProductById")
            val getMappingAnnotation = annotations.firstOrNull { it is GetMapping } as GetMapping

            getMappingAnnotation shouldNotBe null
            getMappingAnnotation.value.firstOrNull { it == "/{id}" } shouldNotBe null
        }
    }
})