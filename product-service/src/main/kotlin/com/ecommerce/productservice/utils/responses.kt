package com.ecommerce.productservice.utils

import com.ecommerce.productservice.constants.StatusResponses
import com.ecommerce.productservice.dto.response.ErrorResponse
import com.ecommerce.productservice.dto.response.SuccessResponse
import org.springframework.http.HttpStatus

fun createSuccessResponse(message: String, data: Any?): SuccessResponse {
    return SuccessResponse(
        status = StatusResponses.SUCCESS,
        code = HttpStatus.OK,
        message = message,
        data = data
    )
}

fun createErrorResponse(code: HttpStatus, message: String): ErrorResponse {
    return ErrorResponse(
        status = StatusResponses.ERROR,
        code = code,
        message = message,
    )
}