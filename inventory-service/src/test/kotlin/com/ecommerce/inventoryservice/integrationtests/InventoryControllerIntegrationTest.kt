package com.ecommerce.inventoryservice.integrationtests

import com.ecommerce.inventoryservice.constants.MessageResponses
import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.models.Inventory
import com.ecommerce.inventoryservice.models.InventoryRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
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
        private val postgreSQLContainer = PostgreSQLContainer("postgres:latest")
            .withDatabaseName("inventory")
            .withInitScript("create-inventory-table.sql")
    }

    private lateinit var inventory: Inventory

    @BeforeEach
    internal fun setUp() {
        inventory = Inventory(
            id = 1,
            skuCode = "test_code",
            quantity = 10
        ).also(inventoryRepository::save)
    }

    @AfterEach
    internal fun tearDown() {
        inventoryRepository.deleteAll()
    }

    @Test
    internal fun shouldBeAbleToReturnInventoryBySkuCode() {
        mockMvc.perform(get("/inventory/{sku-code}", inventory.skuCode))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(StatusResponses.SUCCESS.name))
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.name))
            .andExpect(
                jsonPath("$.message")
                    .value(MessageResponses.INVENTORY_FETCHED_SUCCESS.message)
            )
    }

    @Test
    internal fun shouldBeAbleToReturnErrorWhenInventoryIsNotFoundBySkuCode() {
        val wrongSkuCode = "wrong_skucode"
        mockMvc.perform(get("/inventory/{sku-code}", wrongSkuCode))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(StatusResponses.ERROR.name))
            .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.name))
            .andExpect(
                jsonPath("$.message")
                    .value("${MessageResponses.INVENTORY_NOT_FOUND.message} with skuCode $wrongSkuCode")
            )
    }
}