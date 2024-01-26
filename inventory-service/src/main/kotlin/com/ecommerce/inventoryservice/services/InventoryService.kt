package com.ecommerce.inventoryservice.services

import com.ecommerce.inventoryservice.constants.MessageResponses
import com.ecommerce.inventoryservice.exceptions.InventoryNotFoundException
import com.ecommerce.inventoryservice.models.Inventory
import com.ecommerce.inventoryservice.models.InventoryRepository
import org.springframework.stereotype.Service

@Service
class InventoryService(private val inventoryRepository: InventoryRepository) {
    fun getInventoryBySkuCode(skuCode: String): Inventory {
        return inventoryRepository
            .findInventoryBySkuCode(skuCode)
            .orElseThrow {
                InventoryNotFoundException("${MessageResponses.INVENTORY_NOT_FOUND.message} with skuCode $skuCode")
            }
    }
}
