package com.ecommerce.inventoryservice.models.inbox

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "inbox")
data class Inbox(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    var eventId: UUID,
    var eventType: String,
    var topic: String
)
