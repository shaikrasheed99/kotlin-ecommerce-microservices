package com.ecommerce.inventoryservice.unittests.services

import com.ecommerce.inventoryservice.exceptions.InventoryNotFoundException
import com.ecommerce.inventoryservice.models.InventoryRepository
import com.ecommerce.inventoryservice.services.InventoryService
import com.ecommerce.inventoryservice.utils.TestUtils.createInventory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.stereotype.Service
import java.util.Optional

class InventoryServiceTest : DescribeSpec({
    val mockInventoryRepository = mockk<InventoryRepository>()
    val inventoryService = InventoryService(mockInventoryRepository)

    val inventory = createInventory()

    describe("Inventory Service - annotations") {
        it("should have Service annotation to the Inventory Service class") {
            val classAnnotations = inventoryService.javaClass.annotations
            val serviceAnnotation = classAnnotations.firstOrNull { it is Service } as Service

            serviceAnnotation shouldNotBe null
        }
    }

    describe("Get Inventory by SkuCode") {
        it("should be able to return inventory by SkuCode") {
            every {
                mockInventoryRepository.findInventoryBySkuCode(inventory.skuCode)
            } returns Optional.ofNullable(inventory)

            val inventoryBySkuCode = inventoryService.getInventoryBySkuCode(inventory.skuCode)

            inventoryBySkuCode shouldBe inventory
            verify { mockInventoryRepository.findInventoryBySkuCode(inventory.skuCode) }
        }
    }

    describe("Get Inventory by SkuCode - Error scenarios") {
        it("should throw exception when the Inventory is not found by skuCode") {
            val wrongSkuCode = "wrong-skucode"
            every { mockInventoryRepository.findInventoryBySkuCode(wrongSkuCode) } returns Optional.empty()

            shouldThrow<InventoryNotFoundException> { inventoryService.getInventoryBySkuCode(wrongSkuCode) }

            verify { mockInventoryRepository.findInventoryBySkuCode(wrongSkuCode) }
        }
    }
})
