package com.ecommerce.inventoryservice.integrationtests

import com.ecommerce.inventoryservice.constants.MessageResponses
import com.ecommerce.inventoryservice.constants.StatusResponses
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
@Testcontainers
internal class HealthControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc;

    companion object {
        @Container
        @ServiceConnection
        private val postgreSQLContainer = PostgreSQLContainer("postgres:latest")
            .withDatabaseName("inventory")
            .withInitScript("create-inventory-table.sql")
    }

    @Test
    internal fun shouldBeAbleToReturnSuccessResponseWhenServerIsUp() {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(StatusResponses.SUCCESS.name))
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.message").value(MessageResponses.SERVER_UP.message))
    }
}