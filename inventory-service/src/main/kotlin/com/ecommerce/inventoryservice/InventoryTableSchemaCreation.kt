package com.ecommerce.inventoryservice

import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Component
class InventoryTableSchemaCreation(private val jdbcTemplate: JdbcTemplate) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val inventoryTablePathResource = ClassPathResource("create-inventory-table.sql")
        val inboxTablePathResource = ClassPathResource("create-inbox-table.sql")

        val inventoryTableInputStreamReader = InputStreamReader(
            inventoryTablePathResource.inputStream,
            StandardCharsets.UTF_8
        )

        val inboxTableInputStreamReader = InputStreamReader(
            inboxTablePathResource.inputStream,
            StandardCharsets.UTF_8
        )

        val inventoryTableQuery = FileCopyUtils.copyToString(inventoryTableInputStreamReader)
        val inboxTableQuery = FileCopyUtils.copyToString(inboxTableInputStreamReader)

        jdbcTemplate.execute(inventoryTableQuery)
        jdbcTemplate.execute(inboxTableQuery)
    }
}
