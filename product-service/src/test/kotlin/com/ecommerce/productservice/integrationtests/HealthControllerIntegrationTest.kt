package com.ecommerce.productservice.integrationtests

import com.ecommerce.productservice.constants.MessageResponses
import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.utils.TestUtils.assertCommonResponseBody
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
internal class HealthControllerIntegrationTest(private var mockMvc: MockMvc) : DescribeSpec({
    describe("Health Controller") {
        it("should be able to return Success Response when Server is up") {
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
})
