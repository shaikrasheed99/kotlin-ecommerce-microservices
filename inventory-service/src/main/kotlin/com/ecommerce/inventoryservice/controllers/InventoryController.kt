package com.ecommerce.inventoryservice.controllers

import com.ecommerce.inventoryservice.constants.MessageResponses
import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.dto.responses.Response
import com.ecommerce.inventoryservice.services.InventoryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/inventory")
class InventoryController(private val inventoryService: InventoryService) {
    @GetMapping("/{sku-code}")
    fun getInventoryBy(
        @PathVariable("sku-code") skuCode: String
    ): ResponseEntity<Response> {
        val inventory = inventoryService.getInventoryBy(skuCode)

        val message = MessageResponses.INVENTORY_FETCHED_SUCCESS.message
        val response = Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = message,
            data = inventory
        )

        logger.info("$message of skuCode $skuCode")

        return ResponseEntity.ok(response)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}