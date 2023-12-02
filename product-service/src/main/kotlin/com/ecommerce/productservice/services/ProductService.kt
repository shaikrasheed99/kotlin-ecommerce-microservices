package com.ecommerce.productservice.services

import com.ecommerce.productservice.dto.requests.ProductRequestBody
import com.ecommerce.productservice.models.Product
import com.ecommerce.productservice.models.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {
    fun createProduct(productRequestBody: ProductRequestBody): Product {
        val newProduct = Product(
            id = null,
            name = productRequestBody.name,
            description = productRequestBody.description,
            price = productRequestBody.price,
        )

        return productRepository.save(newProduct)
    }

    fun getAllProducts(): MutableList<Product> {
        return productRepository.findAll()
    }
}
