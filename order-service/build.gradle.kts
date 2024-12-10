import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.owasp.dependencycheck") version "10.0.3"

    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    jacoco
}

group = "com.ecommerce"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val kotlinXJsonVersion = "1.7.1"
val eurekaClientVersion = "4.1.3"
val cloudEventsJsonVersion = "4.0.1"
val detektVersion = "1.23.6"
val mockkVersion = "1.13.11"
val kotestJunit5Version = "5.9.1"
val kotestSpringVersion = "1.3.0"
val kotestJsonVersion = "5.9.1"
val resilience4jVersion = "2.2.0"
val testcontainersMockserverVersion = "1.19.8"
val mockserverNettyVersion = "5.15.0"
val shedLockVersion = "6.0.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.mock-server:mockserver-netty:$mockserverNettyVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinXJsonVersion")
    implementation("com.h2database:h2")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:$eurekaClientVersion")
    implementation("io.github.resilience4j:resilience4j-spring-boot2:$resilience4jVersion")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.cloudevents:cloudevents-json-jackson:$cloudEventsJsonVersion")

    implementation("net.javacrumbs.shedlock:shedlock-spring:$shedLockVersion")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:$shedLockVersion")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestJunit5Version")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestSpringVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestJsonVersion")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:mockserver:$testcontainersMockserverVersion")

    testImplementation("org.springframework.kafka:spring-kafka-test")
}

dependencyCheck {
    analyzers {
        centralEnabled = false
    }
    suppressionFile = "./suppression-file.xml"
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootJar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

jacoco {
    toolVersion = "0.8.9"
    reportsDirectory.set(layout.buildDirectory.dir("jacoco"))
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(
                "**/OrderServiceApplication*.*",
                "**/OrderTableSchemaCreation*.*",
                "**/configs",
                "**/constants"
            )
        }
    )
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    val minimumThreadValue = 0.99.toBigDecimal()
    violationRules {
        rule {
            element = "CLASS"
            classDirectories.setFrom(
                sourceSets.main.get().output.asFileTree.matching {
                    exclude(
                        "**/OrderServiceApplication*.*",
                        "**/OrderTableSchemaCreation*.*",
                        "**/configs",
                        "**/constants"
                    )
                }
            )
            limit {
                counter = "LINE"
                minimum = minimumThreadValue
            }
            limit {
                counter = "BRANCH"
                minimum = minimumThreadValue
            }
            limit {
                counter = "METHOD"
                minimum = minimumThreadValue
            }
            limit {
                counter = "CLASS"
                minimum = minimumThreadValue
            }
            limit {
                counter = "COMPLEXITY"
                minimum = minimumThreadValue
            }
        }
    }
}

tasks.register<io.gitlab.arturbosch.detekt.Detekt>("detektFix") {
    description = "Runs detekt analysis and fixes formatting errors"
    autoCorrect = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    setSource(files("src"))
    reports {
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
    include("**/*.kt")
    include("**/*.kts")
    exclude("resources/")
    exclude("build/")
}

configurations.matching { it.name == "detekt" }.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("1.9.23")
        }
    }
}

springBoot {
    buildInfo()
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName = "shaikrasheed99/order-service:latest"
    builder = "paketobuildpacks/builder:base"
    environment = mapOf(
        "BP_JVM_VERSION" to "17"
    )
}
