package com.ecommerce.inventoryservice.models

import jakarta.persistence.*

@Entity
@Table(name = "inventory")
data class Inventory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int?,
    var skuCode: String,
    var quantity: Int
)