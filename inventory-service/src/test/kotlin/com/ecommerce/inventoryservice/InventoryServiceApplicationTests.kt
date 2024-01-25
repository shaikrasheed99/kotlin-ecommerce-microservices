package com.ecommerce.inventoryservice

import com.ecommerce.inventoryservice.utils.TestUtils.getPostgreSQLContainer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class InventoryServiceApplicationTests {

    companion object {
        @Container
        @ServiceConnection
        private val postgreSQLContainer = getPostgreSQLContainer()
    }

    @Test
    fun contextLoads() {
    }

}
