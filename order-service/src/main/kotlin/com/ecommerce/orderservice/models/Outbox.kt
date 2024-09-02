package com.ecommerce.orderservice.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "outbox")
data class Outbox(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var eventId: UUID?,
    var eventType: String,
    var eventPayload: String,
    var topic: String
)
