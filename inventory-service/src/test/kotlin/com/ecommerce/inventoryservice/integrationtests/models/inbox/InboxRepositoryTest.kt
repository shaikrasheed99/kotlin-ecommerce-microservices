package com.ecommerce.inventoryservice.integrationtests.models.inbox

import com.ecommerce.inventoryservice.models.inbox.InboxRepository
import com.ecommerce.inventoryservice.utils.TestUtils.createInbox
import com.ecommerce.inventoryservice.utils.TestUtils.getPostgreSQLContainerWithMultipleScripts
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class InboxRepositoryTest {
    @Autowired
    private lateinit var inboxRepository: InboxRepository

    companion object {
        @Container
        @ServiceConnection
        val postgreSQLContainer = getPostgreSQLContainerWithMultipleScripts(
            "create-inventory-table.sql",
            "create-inbox-table.sql"
        )
    }

    @AfterEach
    fun tearDown() {
        inboxRepository.deleteAll()
    }

    @Test
    internal fun shouldBeAbleToReturnInboxEventByEventId() {
        val expectedEventId = UUID.randomUUID()
        val inboxOne = createInbox(eventId = expectedEventId)
        val inboxTwo = createInbox()
        inboxRepository.saveAll(listOf(inboxOne, inboxTwo))

        val actualInbox = inboxRepository.findByEventId(expectedEventId)

        actualInbox.get().eventId shouldBe expectedEventId
    }
}
