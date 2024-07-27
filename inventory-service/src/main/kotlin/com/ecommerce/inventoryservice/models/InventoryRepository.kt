package com.ecommerce.inventoryservice.models

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InventoryRepository : JpaRepository<Inventory, Int> {
    @Query(value = "SELECT i FROM Inventory as i WHERE i.skuCode = :skuCode")
    fun findInventoryBySkuCode(@Param("skuCode") skuCode: String): Optional<Inventory>
}
