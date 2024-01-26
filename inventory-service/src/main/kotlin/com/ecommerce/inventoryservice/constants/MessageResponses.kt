package com.ecommerce.inventoryservice.constants

enum class MessageResponses(val message: String) {
    SERVER_UP("Server is up!!"),
    INVENTORY_FETCHED_SUCCESS("Inventory details fetched successfully"),
    INVENTORY_NOT_FOUND("Inventory not found")
}
