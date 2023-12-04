package com.ecommerce.inventoryservice.models

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InventoryRepository : JpaRepository<Inventory, Int> {
    @Query(value = "SELECT * from inventory where sku_code = :skuCode", nativeQuery = true)
    fun findInventoryBy(skuCode: String): Optional<Inventory>
}