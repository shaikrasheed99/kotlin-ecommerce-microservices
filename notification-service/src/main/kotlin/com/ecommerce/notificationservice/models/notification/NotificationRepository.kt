package com.ecommerce.notificationservice.models.notification

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findFirstByOrderByCreatedAtDesc(): List<Notification>
}
