package com.ecommerce.orderservice.utils

import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.responses.InventoryResponse
import com.ecommerce.orderservice.dto.responses.Response
import com.fasterxml.jackson.databind.ObjectMapper
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType
import org.mockserver.verify.VerificationTimes.exactly
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

object InventoryServiceMockServerStubUtils {
    fun invokeGetInventoryBySkuCodeAPIResponse200(mockServer: MockServerClient) {
        val responseJson = getInventorySuccessResponseJson()

        mockServer
            .`when`(
                request()
                    .withMethod(HttpMethod.GET.name())
                    .withPath("/inventory/.*")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseJson)
            )
    }

    fun invokeGetInventoryBySkuCodeWithInsufficientQuantityAPIResponse200(mockServer: MockServerClient) {
        val responseJson = getInventoryWithInsufficientQuantityResponseJson()

        mockServer
            .`when`(
                request()
                    .withMethod(HttpMethod.GET.name())
                    .withPath("/inventory/.*")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseJson)
            )
    }

    fun invokeGetInventoryBySkuCodeAPIResponse404(mockServer: MockServerClient) {
        val responseJson = getInventoryNotFoundResponseJson()

        mockServer
            .`when`(
                request()
                    .withMethod(HttpMethod.GET.name())
                    .withPath("/inventory/.*")
            ).respond(
                response()
                    .withStatusCode(HttpStatus.NOT_FOUND.value())
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody(responseJson)
            )
    }

    fun invokeInventoryServiceNotAvailableAPIResponse(mockServer: MockServerClient) {
        mockServer
            .`when`(
                request()
                    .withMethod(HttpMethod.GET.name())
                    .withPath("/inventory/.*")
            ).respond(
                response()
                    .withStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
            )
    }

    fun verifyGetInventoryBySkuCodeAPICall(mockServer: MockServerClient, numberOfCalls: Int) {
        mockServer.verify(
            request().withMethod(HttpMethod.GET.name()).withPath("/inventory/.*"),
            exactly(numberOfCalls)
        )
    }

    private fun getInventorySuccessResponseJson(): String? = ObjectMapper().writeValueAsString(
        Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = "success response",
            data = InventoryResponse(
                id = 1,
                skuCode = "test_code",
                quantity = 2
            )
        )
    )

    private fun getInventoryWithInsufficientQuantityResponseJson(): String? = ObjectMapper().writeValueAsString(
        Response(
            status = StatusResponses.SUCCESS,
            code = HttpStatus.OK,
            message = "success response",
            data = InventoryResponse(
                id = 1,
                skuCode = "test_code",
                quantity = 1
            )
        )
    )

    private fun getInventoryNotFoundResponseJson(): String? = ObjectMapper().writeValueAsString(
        Response(
            status = StatusResponses.ERROR,
            code = HttpStatus.NOT_FOUND,
            message = "inventory not found",
            data = null
        )
    )
}
