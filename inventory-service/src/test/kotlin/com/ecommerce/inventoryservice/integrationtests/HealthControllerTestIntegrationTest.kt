package com.ecommerce.inventoryservice.integrationtests

import com.ecommerce.inventoryservice.constants.MessageResponses
import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.utils.TestUtils.assertCommonResponseBody
import com.ecommerce.inventoryservice.utils.TestUtils.getPostgreSQLContainer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
@Testcontainers
internal class HealthControllerTestIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc;

    companion object {
        @Container
        @ServiceConnection
        val postgreSQLContainer = getPostgreSQLContainer()
    }

    @Test
    internal fun shouldBeAbleToReturnSuccessResponseWhenServerIsUp() {
        mockMvc.get("/health")
            .andExpect {
                status { isOk() }
                assertCommonResponseBody(
                    status = StatusResponses.SUCCESS,
                    code = HttpStatus.OK,
                    message = MessageResponses.SERVER_UP.message
                )
            }
    }
}