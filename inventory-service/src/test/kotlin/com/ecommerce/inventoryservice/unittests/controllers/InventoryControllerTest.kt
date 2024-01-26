package com.ecommerce.inventoryservice.unittests.controllers

import com.ecommerce.inventoryservice.constants.MessageResponses
import com.ecommerce.inventoryservice.controllers.InventoryController
import com.ecommerce.inventoryservice.exceptions.InventoryNotFoundException
import com.ecommerce.inventoryservice.services.InventoryService
import com.ecommerce.inventoryservice.utils.EntityUtils.getMethodAnnotations
import com.ecommerce.inventoryservice.utils.EntityUtils.getMethodParameterAnnotations
import com.ecommerce.inventoryservice.utils.TestUtils.assertCommonFields
import com.ecommerce.inventoryservice.utils.TestUtils.createInventory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

class InventoryControllerTest : DescribeSpec({
    val mockInventoryService = mockk<InventoryService>()
    val inventoryController = InventoryController(mockInventoryService)

    val inventory = createInventory()

    describe("Inventory Controller - annotations") {
        it("should have RestController & RequestMapping annotations to the Inventory Controller class") {
            val classAnnotations = inventoryController.javaClass.annotations
            val restControllerAnnotation = classAnnotations.firstOrNull { it is RestController } as RestController
            val requestMappingAnnotation = classAnnotations.firstOrNull { it is RequestMapping } as RequestMapping

            restControllerAnnotation shouldNotBe null

            requestMappingAnnotation shouldNotBe null
            requestMappingAnnotation.value.firstOrNull { it == "/inventory" } shouldNotBe null
        }
    }

    describe("Get Inventory by SkuCode") {
        it("should be able to return Inventory by SkuCode") {
            every { mockInventoryService.getInventoryBy(inventory.skuCode) } returns inventory

            val response = inventoryController.getInventoryBy(inventory.skuCode).body

            assertCommonFields(
                response = response,
                message = MessageResponses.INVENTORY_FETCHED_SUCCESS.message,
                data = inventory
            )

            verify {
                mockInventoryService.getInventoryBy(inventory.skuCode)
            }
        }
    }

    describe("Get Inventory by SkuCode - Error scenarios") {
        it("should be able to throw Inventory Not Found Exception when Inventory is not present by SkuCode") {
            val inventoryNotFoundException = InventoryNotFoundException("inventory not found")
            every { mockInventoryService.getInventoryBy(inventory.skuCode) } throws inventoryNotFoundException

            shouldThrow<InventoryNotFoundException> { inventoryController.getInventoryBy(inventory.skuCode) }

            verify {
                mockInventoryService.getInventoryBy(inventory.skuCode)
            }
        }
    }

    describe("Get Inventory by SkuCode - annotations") {
        it("should have GetMapping annotation with path value to the getInventoryBy method") {
            val annotations = inventoryController.getMethodAnnotations("getInventoryBy")
            val getMappingAnnotation = annotations.firstOrNull { it is GetMapping } as GetMapping

            getMappingAnnotation shouldNotBe null
            getMappingAnnotation.value.firstOrNull { it == "/{sku-code}" } shouldNotBe null
        }

        it("should have PathVariable annotation to parameter of the getInventoryBy method") {
            val annotations = inventoryController.getMethodParameterAnnotations(
                "getInventoryBy",
                "skuCode"
            )
            val pathVariableAnnotation = annotations.firstOrNull { it is PathVariable } as PathVariable

            pathVariableAnnotation shouldNotBe null
            pathVariableAnnotation.value shouldBe "sku-code"
        }
    }

    describe("Inventory Controller - logger") {
        it("should initialize the logger of inventory controller") {
            InventoryController.logger shouldNotBe null
        }
    }
})