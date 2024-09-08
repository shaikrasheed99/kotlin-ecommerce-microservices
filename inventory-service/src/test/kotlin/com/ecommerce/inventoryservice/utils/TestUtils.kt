package com.ecommerce.inventoryservice.utils

import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.dto.responses.Response
import com.ecommerce.inventoryservice.events.OrderPlacedEvent
import com.ecommerce.inventoryservice.models.inbox.Inbox
import com.ecommerce.inventoryservice.models.inventory.Inventory
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.sql.DriverManager
import java.util.UUID

object TestUtils {
    fun getPostgreSQLContainerWithTableScript(script: String): PostgreSQLContainer<*>? =
        PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("inventory")
            .withInitScript(script)

    fun getPostgreSQLContainerWithMultipleScripts(vararg scripts: String): PostgreSQLContainer<*>? {
        val container = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("inventory")
            .waitingFor(Wait.forListeningPort())

        container.start()

        container.waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1))

        val connection = DriverManager.getConnection(
            container.jdbcUrl,
            container.username,
            container.password
        )

        scripts.forEach { script ->
            val scriptContent = this::class.java.classLoader.getResource(script)!!.readText()
            connection.createStatement().use { stmt ->
                stmt.execute(scriptContent)
            }
        }

        return container
    }

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

    fun createInbox(
        id: UUID = UUID.randomUUID(),
        eventId: UUID = UUID.randomUUID(),
        eventType: String = "test_type",
        topic: String = "test_topic"
    ) = Inbox(
        id = id,
        eventId = eventId,
        eventType = eventType,
        topic = topic
    )
}
