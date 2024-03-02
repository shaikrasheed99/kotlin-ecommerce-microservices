package com.ecommerce.notificationservice.services

import com.ecommerce.notificationservice.models.Notification
import com.ecommerce.notificationservice.models.NotificationRepository
import org.springframework.stereotype.Service

@Service
class NotificationService(private val notificationRepository: NotificationRepository) {
    fun getRecentNotification(): List<Notification> {
        return notificationRepository.findRecentNotification()
    }
}