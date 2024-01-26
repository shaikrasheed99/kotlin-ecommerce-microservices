package com.ecommerce.inventoryservice

import com.ecommerce.inventoryservice.models.Inventory
import com.ecommerce.inventoryservice.models.InventoryRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class InventoryServiceApplication {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun loadInventoryData(inventoryRepository: InventoryRepository): CommandLineRunner {
        return CommandLineRunner {
            if (inventoryRepository.count() == 0L) {
                val onePlus = Inventory(
                    id = 1,
                    skuCode = "one_plus",
                    quantity = 10
                )

                val iphone = Inventory(
                    id = 2,
                    skuCode = "iphone",
                    quantity = 0
                )

                inventoryRepository.saveAll(listOf(onePlus, iphone))
                logger.info("Inventory data has been saved successfully")
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<InventoryServiceApplication>(args = args)
}
