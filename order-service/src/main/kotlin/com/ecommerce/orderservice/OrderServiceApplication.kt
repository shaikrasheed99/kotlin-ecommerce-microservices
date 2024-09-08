package com.ecommerce.orderservice

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
class OrderServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(args = args)
}
