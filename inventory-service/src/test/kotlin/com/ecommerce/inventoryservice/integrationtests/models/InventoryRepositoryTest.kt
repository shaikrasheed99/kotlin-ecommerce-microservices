package com.ecommerce.inventoryservice.integrationtests.models

import com.ecommerce.inventoryservice.models.InventoryRepository
import com.ecommerce.inventoryservice.utils.TestUtils
import com.ecommerce.inventoryservice.utils.TestUtils.createInventory
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class InventoryRepositoryTest {
    @Autowired
    lateinit var inventoryRepository: InventoryRepository

    companion object {
        @Container
        @ServiceConnection
        val postgreSQLContainer = TestUtils.getPostgreSQLContainer()
    }

    @AfterEach
    fun tearDown() {
        inventoryRepository.deleteAll()
    }

    @Test
    internal fun shouldBeAbleToReturnInventoryBySkuCode() {
        val inventory = createInventory()
        inventoryRepository.save(inventory)

        val inventoryBySkuCode = inventoryRepository.findInventoryBySkuCode(inventory.skuCode)

        inventoryBySkuCode.get().id shouldBe inventory.id
        inventoryBySkuCode.get().skuCode shouldBe inventory.skuCode
        inventoryBySkuCode.get().quantity shouldBe inventory.quantity
    }

    @Test
    internal fun shouldNotThrowExceptionWhenInventoryIsNotPresentBySkuCode() {
        val wrongSkuCode = "wrong_skucode"

        shouldNotThrow<Exception> {
            inventoryRepository.findInventoryBySkuCode(wrongSkuCode)
        }
    }
}
