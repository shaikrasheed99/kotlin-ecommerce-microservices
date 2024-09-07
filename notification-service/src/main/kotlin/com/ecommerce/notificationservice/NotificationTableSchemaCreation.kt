package com.ecommerce.notificationservice

import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Component
class NotificationTableSchemaCreation(private val jdbcTemplate: JdbcTemplate) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val notificationsTablePathResource = ClassPathResource("create-notifications-table.sql")
        val inboxTablePathResource = ClassPathResource("create-inbox-table.sql")

        val notificationsTableInputStreamReader = InputStreamReader(
            notificationsTablePathResource.inputStream,
            StandardCharsets.UTF_8
        )
        val inboxTableInputStreamReader = InputStreamReader(
            inboxTablePathResource.inputStream,
            StandardCharsets.UTF_8
        )

        val notificationsTableQuery = FileCopyUtils.copyToString(notificationsTableInputStreamReader)
        val inboxTableQuery = FileCopyUtils.copyToString(inboxTableInputStreamReader)

        jdbcTemplate.execute(notificationsTableQuery)
        jdbcTemplate.execute(inboxTableQuery)
    }
}
