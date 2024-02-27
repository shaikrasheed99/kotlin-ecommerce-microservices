package com.ecommerce.notificationservice.unittests.consumer

import com.ecommerce.notificationservice.consumer.KafkaConsumer
import com.ecommerce.notificationservice.utils.EntityUtils.getMethodAnnotations
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

class KafkaConsumerTest : DescribeSpec({
    val kafkaConsumer = KafkaConsumer()

    describe("Kafka Consumer - annotations") {
        it("should have Component annotation to the kafka consumer class") {
            val annotations = kafkaConsumer.javaClass.annotations
            val componentAnnotation = annotations.firstOrNull { it is Component } as Component

            componentAnnotation shouldNotBe null
        }
    }

    describe("handleOrderPlacedEvent - annotations") {
        it("should have KafkaListener annotation to the handleOrderPlacedEvent method") {
            val methodAnnotations = kafkaConsumer.getMethodAnnotations("handleOrderPlacedEvent")
            val kafkaListenerAnnotation = methodAnnotations.firstOrNull { it is KafkaListener } as KafkaListener

            kafkaListenerAnnotation shouldNotBe null
            kafkaListenerAnnotation.topics.first() shouldBe "notificationsTopic"
        }
    }

    describe("Kafka Consumer - logger") {
        it("should initialize the logger of kafka consumer") {
            KafkaConsumer.logger shouldNotBe null
        }
    }
})
