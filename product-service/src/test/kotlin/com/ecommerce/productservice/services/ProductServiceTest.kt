package com.ecommerce.productservice.services

import com.ecommerce.productservice.dto.requests.ProductRequestBody
import com.ecommerce.productservice.models.Product
import com.ecommerce.productservice.models.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
internal class ProductServiceTest {
    @Mock
    private lateinit var productRepository: ProductRepository

    @InjectMocks
    private lateinit var productService: ProductService

    @Test
    internal fun shouldBeAbleToCreateNewProduct() {
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

        `when`(productRepository.save(any(Product::class.java))).thenReturn(product)

        assertDoesNotThrow { productService.createProduct(productRequestBody) }

        verify(productRepository, times(1)).save(any(Product::class.java))
    }

    @Test
    internal fun shouldBeAbleToReturnListOfProducts() {
        val products = listOf<Product>(
            Product(
                id = 1,
                name = "test name",
                description = "test description",
                price = BigDecimal(10.20)
            )
        )

        `when`(productRepository.findAll()).thenReturn(products)

        val listOfProducts = productService.getAllProducts()

        assertEquals(listOfProducts.size, 1)

        verify(productRepository, times(1)).findAll()
    }
}