package com.ecommerce.orderservice.dto.responses

import com.ecommerce.orderservice.constants.StatusResponses
import org.springframework.http.HttpStatus

data class Response(
    var status: StatusResponses,
    var code: HttpStatus,
    var message: String,
    var data: Any?
)