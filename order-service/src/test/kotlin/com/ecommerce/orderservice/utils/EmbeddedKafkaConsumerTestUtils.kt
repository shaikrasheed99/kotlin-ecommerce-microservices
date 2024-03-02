package com.ecommerce.orderservice.utils

import com.ecommerce.orderservice.events.OrderPlacedEvent
import com.ecommerce.orderservice.integrationtests.DEFAULT_TEST_TOPIC
import io.kotest.matchers.shouldNotBe
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.util.Collections

object EmbeddedKafkaConsumerTestUtils {
    fun assertConsumerRecord(consumer: Consumer<String, OrderPlacedEvent>) {
        val record = KafkaTestUtils.getSingleRecord(consumer, DEFAULT_TEST_TOPIC)
        record.value() shouldNotBe null
    }

    fun createTestConsumer(embeddedKafkaBroker: EmbeddedKafkaBroker): Consumer<String, OrderPlacedEvent> {
        val testConsumerGroup = "test_group"
        val consumerProps = KafkaTestUtils.consumerProps(testConsumerGroup, "true", embeddedKafkaBroker)

        val consumerFactory = DefaultKafkaConsumerFactory<String, OrderPlacedEvent>(consumerProps)
        val consumer = consumerFactory.createConsumer()
        consumer.subscribe(Collections.singletonList(DEFAULT_TEST_TOPIC))

        return consumer
    }
}
