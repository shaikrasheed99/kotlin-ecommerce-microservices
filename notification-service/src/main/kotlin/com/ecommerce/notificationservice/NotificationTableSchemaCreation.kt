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
        val scrips = listOf(
            "create-notifications-table.sql",
            "create-inbox-table.sql"
        )

        scrips.forEach {
            val tablePathResource = ClassPathResource(it)

            val tableInputStreamReader = InputStreamReader(
                tablePathResource.inputStream,
                StandardCharsets.UTF_8
            )

            val tableQuery = FileCopyUtils.copyToString(tableInputStreamReader)

            jdbcTemplate.execute(tableQuery)
        }
    }
}
