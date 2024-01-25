package com.ecommerce.productservice.integrationtests

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.utils.TestUtils.assertCommonResponseBody
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
internal class HealthControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

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
