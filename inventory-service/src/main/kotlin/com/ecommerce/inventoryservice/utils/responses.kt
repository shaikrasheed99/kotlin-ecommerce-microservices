package com.ecommerce.inventoryservice.utils

import com.ecommerce.inventoryservice.constants.StatusResponses
import com.ecommerce.inventoryservice.dto.responses.SuccessResponse
import org.springframework.http.HttpStatus

fun createSuccessResponse(message: String, data: Any?): SuccessResponse {
    return SuccessResponse(
        status = StatusResponses.SUCCESS,
        code = HttpStatus.OK,
        message = message,
        data = data
    )
}