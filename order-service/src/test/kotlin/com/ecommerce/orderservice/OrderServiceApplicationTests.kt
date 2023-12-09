package com.ecommerce.orderservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest
class OrderServiceApplicationTests {
    companion object {
        @JvmStatic
        @DynamicPropertySource
        private fun configure(registry: DynamicPropertyRegistry) {
            registry.add("inventory.service.url") {
                "http://dummyurl"
            }
        }
    }

    @Test
    fun contextLoads() {
    }

}
