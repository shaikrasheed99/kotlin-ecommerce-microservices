package com.ecommerce.orderservice

import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Component
class OrderTableSchemaCreation(private val jdbcTemplate: JdbcTemplate) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val ordersTablePathResource = ClassPathResource("create-orders-table.sql")
        val outboxTablePathResource = ClassPathResource("create-outbox-table.sql")

        val ordersTableInputStreamReader = InputStreamReader(
            ordersTablePathResource.inputStream,
            StandardCharsets.UTF_8
        )
        val outboxTableInputStreamReader = InputStreamReader(
            outboxTablePathResource.inputStream,
            StandardCharsets.UTF_8
        )

        val ordersTableQuery = FileCopyUtils.copyToString(ordersTableInputStreamReader)
        val outboxTableQuery = FileCopyUtils.copyToString(outboxTableInputStreamReader)

        jdbcTemplate.execute(ordersTableQuery)
        jdbcTemplate.execute(outboxTableQuery)
    }
}
