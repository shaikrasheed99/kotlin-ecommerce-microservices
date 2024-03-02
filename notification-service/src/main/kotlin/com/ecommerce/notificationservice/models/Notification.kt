package com.ecommerce.notificationservice.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp
import java.util.UUID

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    var sender: String,
    var recipient: String,
    var isSent: Boolean,
    var orderId: UUID,
    var skuCode: String,
    var createdAt: Timestamp
)
