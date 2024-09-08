package com.ecommerce.orderservice.unittests.jobs

import com.ecommerce.orderservice.jobs.PublishEventsJob
import com.ecommerce.orderservice.services.PublishEventsService
import com.ecommerce.orderservice.utils.EntityUtils.getMethodAnnotations
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import net.javacrumbs.shedlock.core.LockAssert.TestHelper.makeAllAssertsPass
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

class PublishEventsJobTest : DescribeSpec({
    val mockPublishEventsService = mockk<PublishEventsService>()
    val publishEventsJob = PublishEventsJob(mockPublishEventsService)

    afterEach {
        clearAllMocks()
        unmockkAll()
    }

    describe("PublishEventsJob - annotations") {
        it("should have Component annotation to the PublishEventsJob class") {
            val classAnnotations = publishEventsJob.javaClass.annotations
            val componentAnnotation = classAnnotations.firstOrNull { it is Component } as Component

            componentAnnotation shouldNotBe null
        }
    }

    describe("PublishEventsJob - publish") {
        it("should execute the job and call the publish method") {
            every { mockPublishEventsService.publish() } just runs
            makeAllAssertsPass(true)

            publishEventsJob.publish()

            verify(exactly = 1) {
                mockPublishEventsService.publish()
            }
        }
    }

    describe("PublishEventsJob - publish - Error scenarios") {
        it("should throw exception when error occurred while publishing") {
            val exception = RuntimeException("exception while publishing")
            every { mockPublishEventsService.publish() } throws exception
            makeAllAssertsPass(true)

            shouldThrow<RuntimeException> { publishEventsJob.publish() }

            verify(exactly = 1) {
                mockPublishEventsService.publish()
            }
        }
    }

    describe("PublishEventsJob - publish - annotations") {
        it("should have Scheduled annotation to the publish method") {
            val annotations = publishEventsJob.getMethodAnnotations("publish")
            val scheduledAnnotation = annotations.firstOrNull { it is Scheduled } as Scheduled

            scheduledAnnotation shouldNotBe null
            scheduledAnnotation.cron shouldBe "\${jobs.publish-events.cron}"
        }

        it("should have SchedulerLock annotation to the publish method") {
            val annotations = publishEventsJob.getMethodAnnotations("publish")
            val schedulerLockAnnotation = annotations.firstOrNull { it is SchedulerLock } as SchedulerLock

            schedulerLockAnnotation shouldNotBe null
            schedulerLockAnnotation.name shouldBe "publish-events-job-lock"
        }
    }

    describe("PublishEventsJob - logger") {
        it("should initialize the logger of PublishEventsJob") {
            PublishEventsJob.logger shouldNotBe null
        }
    }
})
