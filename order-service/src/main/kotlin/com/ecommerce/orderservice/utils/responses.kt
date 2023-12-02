package com.ecommerce.orderservice.utils

import com.ecommerce.orderservice.constants.StatusResponses
import com.ecommerce.orderservice.dto.responses.SuccessResponse
import org.springframework.http.HttpStatus

fun createSuccessResponse(message: String, data: Any?): SuccessResponse {
    return SuccessResponse(
        status = StatusResponses.SUCCESS,
        code = HttpStatus.OK,
        message = message,
        data = data
    )
}