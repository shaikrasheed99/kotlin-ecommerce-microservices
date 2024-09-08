package com.ecommerce.inventoryservice.utils

import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.dto.responses.Response
import com.ecommerce.inventoryservice.events.OrderPlacedEvent
import com.ecommerce.inventoryservice.models.inventory.Inventory
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.testcontainers.containers.PostgreSQLContainer
import java.util.UUID

object TestUtils {
    fun getPostgreSQLContainer(): PostgreSQLContainer<*>? =
        PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("inventory")
            .withInitScript("create-inventory-table.sql")

    fun createInventory(
        quantity: Int = 10
    ) = Inventory(
        id = 1,
        skuCode = "test_code",
        quantity = quantity
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

    fun assertCommonFields(
        response: Response?,
        status: StatusResponses = StatusResponses.SUCCESS,
        code: HttpStatus = HttpStatus.OK,
        message: String,
        data: Any
    ) {
        response?.status shouldBe status
        response?.code shouldBe code
        response?.message shouldContain message
        response?.data shouldBe data
    }

    fun createTestOrderPlacedEvent(): OrderPlacedEvent = OrderPlacedEvent(
        orderId = UUID.fromString("aaa8a937-0504-4468-823a-04ccd6964d10"),
        skuCode = "test_skuCode",
        quantity = 10
    )
}
