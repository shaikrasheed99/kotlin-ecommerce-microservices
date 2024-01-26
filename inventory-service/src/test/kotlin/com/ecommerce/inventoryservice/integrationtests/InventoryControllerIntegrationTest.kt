package com.ecommerce.inventoryservice.integrationtests

import com.ecommerce.inventoryservice.constants.MessageResponses
import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.models.Inventory
import com.ecommerce.inventoryservice.models.InventoryRepository
import com.ecommerce.inventoryservice.utils.TestUtils.assertCommonResponseBody
import com.ecommerce.inventoryservice.utils.TestUtils.createInventory
import com.ecommerce.inventoryservice.utils.TestUtils.getPostgreSQLContainer
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
internal class InventoryControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var inventoryRepository: InventoryRepository

    companion object {
        @Container
        @ServiceConnection
        val postgreSQLContainer = getPostgreSQLContainer()
    }

    private lateinit var inventory: Inventory

    @BeforeEach
    fun setUp() {
        inventoryRepository.deleteAll()
    }

    @Test
    internal fun shouldBeAbleToReturnInventoryBySkuCode() {
        inventory = createInventory()
        inventoryRepository.save(inventory)

        mockMvc.get("/inventory/{sku-code}", inventory.skuCode)
            .andExpect {
                status { isOk() }
                assertCommonResponseBody(
                    status = StatusResponses.SUCCESS,
                    code = HttpStatus.OK,
                    message = MessageResponses.INVENTORY_FETCHED_SUCCESS.message
                )
            }

        inventoryRepository.count() shouldBe 1
    }

    @Test
    internal fun shouldBeAbleToReturnErrorWhenInventoryIsNotFoundBySkuCode() {
        val wrongSkuCode = "wrong_skucode"

        mockMvc.get("/inventory/{sku-code}", wrongSkuCode)
            .andExpect {
                status { isNotFound() }
                assertCommonResponseBody(
                    status = StatusResponses.ERROR,
                    code = HttpStatus.NOT_FOUND,
                    message = "${MessageResponses.INVENTORY_NOT_FOUND.message} with skuCode $wrongSkuCode"
                )
            }

        inventoryRepository.count() shouldBe 0
    }
}