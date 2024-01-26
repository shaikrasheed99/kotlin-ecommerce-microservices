package com.ecommerce.orderservice.integrationtests

import com.ecommerce.orderservice.constants.MessageResponses
import com.ecommerce.orderservice.constants.StatusResponses
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
internal class HealthControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc;

    companion object {
        @JvmStatic
        @DynamicPropertySource
        private fun configure(registry: DynamicPropertyRegistry) {
            registry.add("inventory.service.url") {
                "http://dummyurl"
            }
        }
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