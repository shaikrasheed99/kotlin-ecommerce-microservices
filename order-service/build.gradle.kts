import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"

    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
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
    implementation("org.mock-server:mockserver-netty:5.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("com.h2database:h2")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")
    implementation("io.github.resilience4j:resilience4j-spring-boot2:2.2.0")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.cloudevents:cloudevents-json-jackson:2.5.0")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.kotest:kotest-assertions-json:5.9.1")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:mockserver:1.19.3")

    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
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
                "**/configs"
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
                        "**/configs"
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
            useVersion("1.9.0")
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
