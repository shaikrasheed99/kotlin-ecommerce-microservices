package com.ecommerce.inventoryservice.controllers

import com.ecommerce.inventoryservice.constants.MessageResponses
import com.ecommerce.inventoryservice.dto.responses.SuccessResponse
import com.ecommerce.inventoryservice.services.InventoryService
import com.ecommerce.inventoryservice.utils.createSuccessResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/inventory")
class InventoryController(private val inventoryService: InventoryService) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{sku-code}")
    fun getInventoryBy(
        @PathVariable("sku-code") skuCode: String
    ): ResponseEntity<SuccessResponse> {
        val inventory = inventoryService.getInventoryBy(skuCode)

        val message = MessageResponses.INVENTORY_FETCHED_SUCCESS.message
        val successResponse = createSuccessResponse(message, inventory)

        logger.info("$message of skuCode $skuCode")

        return ResponseEntity.ok(successResponse)
    }
}