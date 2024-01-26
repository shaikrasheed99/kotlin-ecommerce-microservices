package com.ecommerce.inventoryservice.dto.responses

import com.ecommerce.inventoryservice.constants.StatusResponses
import org.springframework.http.HttpStatus

data class Response(
    var status: StatusResponses,
    var code: HttpStatus,
    var message: String,
    var data: Any?
)
