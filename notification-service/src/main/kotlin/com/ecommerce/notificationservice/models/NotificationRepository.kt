package com.ecommerce.notificationservice.models

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface NotificationRepository : JpaRepository<Notification, UUID> {
    @Query(value = "SELECT * FROM notifications ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    fun findRecentNotification(): List<Notification>
}
