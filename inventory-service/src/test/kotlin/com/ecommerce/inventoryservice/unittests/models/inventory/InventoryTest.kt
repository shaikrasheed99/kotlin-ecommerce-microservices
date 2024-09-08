package com.ecommerce.inventoryservice.unittests.models.inventory

import com.ecommerce.inventoryservice.models.inventory.Inventory
import com.ecommerce.inventoryservice.utils.EntityUtils.getAttributeAnnotations
import com.ecommerce.inventoryservice.utils.TestUtils.createInventory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

class InventoryTest : DescribeSpec({
    lateinit var inventory: Inventory

    beforeEach {
        inventory = createInventory()
    }

    describe("Inventory - getters") {
        it("should be able to have all the attributes in the inventory") {
            inventory.id shouldBe 1
            inventory.skuCode shouldBe "test_code"
            inventory.quantity shouldBe 10
        }
    }

    describe("Inventory - setters") {
        it("should be able to change Id attribute of the inventory") {
            inventory.id = 100

            inventory.id shouldBe 100
        }

        it("should be able to change Name attribute of the inventory") {
            inventory.skuCode = "another skuCode"

            inventory.skuCode shouldBe "another skuCode"
        }

        it("should be able to change Description attribute of the inventory") {
            inventory.quantity = 20

            inventory.quantity shouldBe 20
        }
    }

    describe("Inventory - annotations") {
        it("should have Entity & Table annotations to Inventory class") {
            val classAnnotations = inventory.javaClass.annotations
            val entityAnnotation = classAnnotations.firstOrNull { it is Entity } as Entity
            val tableAnnotation = classAnnotations.firstOrNull { it is Table } as Table

            entityAnnotation shouldNotBe null

            tableAnnotation shouldNotBe null
            tableAnnotation.name shouldBe "inventory"
        }

        it("should have Id & GeneratedValue annotations to the Id attribute of Inventory class") {
            val annotations = inventory.getAttributeAnnotations("id")
            val idAnnotation = annotations.firstOrNull { it is Id } as Id
            val generatedValueAnnotation = annotations.firstOrNull { it is GeneratedValue } as GeneratedValue

            idAnnotation shouldNotBe null

            generatedValueAnnotation shouldNotBe null
            generatedValueAnnotation.strategy shouldBe GenerationType.IDENTITY
        }
    }
})
