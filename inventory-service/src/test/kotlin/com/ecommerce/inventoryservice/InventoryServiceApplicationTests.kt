package com.ecommerce.inventoryservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class InventoryServiceApplicationTests {

	companion object {
		@Container
		@ServiceConnection
		private val postgreSQLContainer = PostgreSQLContainer("postgres:latest")
			.withDatabaseName("inventory")
			.withInitScript("create-inventory-table.sql")
	}

	@Test
	fun contextLoads() {
	}

}
