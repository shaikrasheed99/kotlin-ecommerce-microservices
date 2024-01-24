package com.ecommerce.productservice

import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Component
class ProductsTableSchemaCreation(private val jdbcTemplate: JdbcTemplate) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val classPathResource = ClassPathResource("create-products-table.sql")
        val inputStreamReader = InputStreamReader(classPathResource.inputStream, StandardCharsets.UTF_8)

        val query = FileCopyUtils.copyToString(inputStreamReader)

        jdbcTemplate.execute(query)
    }
}
