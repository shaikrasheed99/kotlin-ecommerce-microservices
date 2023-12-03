package com.ecommerce.orderservice.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    var skuCode: String,
    var price: BigDecimal,
    var quantity: Int
)
