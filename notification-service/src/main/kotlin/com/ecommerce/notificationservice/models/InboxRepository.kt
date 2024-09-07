package com.ecommerce.notificationservice.models

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface InboxRepository : JpaRepository<Inbox, UUID> {
    fun findByEventId(eventId: UUID): Optional<Inbox>
}
