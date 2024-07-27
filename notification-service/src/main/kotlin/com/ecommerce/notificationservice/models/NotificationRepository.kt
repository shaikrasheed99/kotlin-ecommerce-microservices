package com.ecommerce.notificationservice.models

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface NotificationRepository : JpaRepository<Notification, UUID> {
    @Query(value = "SELECT n FROM Notification as n ORDER BY n.createdAt DESC LIMIT 1")
    fun findRecentNotification(): List<Notification>
}
