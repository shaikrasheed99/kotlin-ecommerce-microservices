package com.ecommerce.inventoryservice.utils

import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.models.Inventory
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.testcontainers.containers.PostgreSQLContainer

object TestUtils {
    fun getPostgreSQLContainer(): PostgreSQLContainer<*>? =
        PostgreSQLContainer("postgres:latest")
            .withDatabaseName("inventory")
            .withInitScript("create-inventory-table.sql")

    fun createInventory() = Inventory(
        id = 1,
        skuCode = "test_code",
        quantity = 10
    )

    fun MockMvcResultMatchersDsl.assertCommonResponseBody(
        status: StatusResponses,
        code: HttpStatus,
        message: String
    ) {
        jsonPath("$.status") { value(status.name) }
        jsonPath("$.code") { value(code.name) }
        jsonPath("$.message") { value(message) }
    }
}