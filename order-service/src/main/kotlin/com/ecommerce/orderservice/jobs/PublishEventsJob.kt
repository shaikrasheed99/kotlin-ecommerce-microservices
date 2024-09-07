package com.ecommerce.orderservice.jobs

import com.ecommerce.orderservice.services.PublishEventsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PublishEventsJob(private val publishEventsService: PublishEventsService) {
    @Scheduled(cron = "\${jobs.publish-events.cron}")
    fun publish() {
        logger.info("Publish Events Job started")
        publishEventsService.publish()
        logger.info("Publish Events Job finished")
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
