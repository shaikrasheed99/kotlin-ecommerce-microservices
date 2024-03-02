package com.ecommerce.notificationservice.dto.responses

import com.ecommerce.notificationservice.constants.StatusResponses
import org.springframework.http.HttpStatus

data class Response(
    var status: StatusResponses,
    var code: HttpStatus,
    var message: String,
    var data: Any?
)
