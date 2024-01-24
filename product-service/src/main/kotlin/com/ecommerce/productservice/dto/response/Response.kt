package com.ecommerce.productservice.dto.response

import com.ecommerce.productservice.constants.StatusResponses
import org.springframework.http.HttpStatus

data class Response(
    var status: StatusResponses,
    var code: HttpStatus,
    var message: String,
    var data: Any?
)
