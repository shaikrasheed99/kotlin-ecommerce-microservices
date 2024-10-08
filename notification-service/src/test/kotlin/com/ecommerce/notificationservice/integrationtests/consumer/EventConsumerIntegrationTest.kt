package com.ecommerce.notificationservice.integrationtests.consumer

import com.ecommerce.notificationservice.models.notification.NotificationRepository
import com.ecommerce.notificationservice.utils.EmbeddedKafkaProducerTestUtils.createTestProducer
import com.ecommerce.notificationservice.utils.TestUtils.createTestOrderPlacedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import java.time.Duration

const val DEFAULT_TEST_TOPIC = "testTopic"

@SpringBootTest
@EmbeddedKafka
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventConsumerIntegrationTest {
    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    private lateinit var testProducer: Producer<String, String>

    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        testProducer = createTestProducer(embeddedKafkaBroker)
        notificationRepository.deleteAll()
    }

    @Test
    fun shouldBeAbleToConsumeOrderPlacedEvents() {
        val orderPlacedEvent = createTestOrderPlacedEvent()
        val payload = mapper.writeValueAsString(orderPlacedEvent)
        val testKey = "testKey"

        testProducer.send(ProducerRecord(DEFAULT_TEST_TOPIC, testKey, payload))

        await().atMost(Duration.ofSeconds(30)).untilAsserted {
            val notification = notificationRepository.findFirstByOrderByCreatedAtDesc()

            if (notification.isNotEmpty()) {
                notification[0].orderId shouldBe orderPlacedEvent.orderId
                notification[0].skuCode shouldBe "test"
            }
        }
    }
}
