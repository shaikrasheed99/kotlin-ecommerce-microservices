package com.ecommerce.inventoryservice.unittests.services

import com.ecommerce.inventoryservice.exceptions.InventoryNotFoundException
import com.ecommerce.inventoryservice.models.Inventory
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
import io.mockk.verifyOrder
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

    describe("Update Inventory Quantity by SkuCode") {
        it("should be able to update inventory quantity by SkuCode") {
            every {
                mockInventoryRepository.findInventoryBySkuCode(inventory.skuCode)
            } returns Optional.ofNullable(inventory)
            val updatedInventory = createInventory(quantity = 20)
            every {
                mockInventoryRepository.save(any(Inventory::class))
            } returns updatedInventory

            inventoryService.updateInventoryQuantityBySkucode(inventory.skuCode, 20)

            verifyOrder {
                mockInventoryRepository.findInventoryBySkuCode(inventory.skuCode)
                mockInventoryRepository.save(any(Inventory::class))
            }
        }
    }

    describe("Update Inventory Quantity by SkuCode - Error scenarios") {
        it("should throw exception when the Inventory is not found by skuCode") {
            val wrongSkuCode = "wrong-skucode"
            every { mockInventoryRepository.findInventoryBySkuCode(wrongSkuCode) } returns Optional.empty()

            shouldThrow<InventoryNotFoundException> {
                inventoryService.updateInventoryQuantityBySkucode(wrongSkuCode, 20)
            }

            verify { mockInventoryRepository.findInventoryBySkuCode(wrongSkuCode) }
        }

        it("should throw exception when the repository throws an exception while updating the inventory") {
            val exception = Exception("exception from repository")
            every {
                mockInventoryRepository.findInventoryBySkuCode(inventory.skuCode)
            } returns Optional.ofNullable(inventory)
            every {
                mockInventoryRepository.save(any(Inventory::class))
            } throws exception

            shouldThrow<Exception> {
                inventoryService.updateInventoryQuantityBySkucode(inventory.skuCode, 20)
            }

            verify {
                mockInventoryRepository.findInventoryBySkuCode(inventory.skuCode)
                mockInventoryRepository.save(any(Inventory::class))
            }
        }
    }
})
