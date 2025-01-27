import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.6"
	id("io.gitlab.arturbosch.detekt") version "1.23.6"
	id("org.owasp.dependencycheck") version "12.0.0"

	kotlin("jvm") version "2.1.0"
	kotlin("plugin.spring") version "2.1.10"
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
val kafkaVersion = "3.3.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinXJsonVersion")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:$eurekaClientVersion")

	implementation("org.springframework.kafka:spring-kafka:$kafkaVersion")
	implementation("io.cloudevents:cloudevents-json-jackson:$cloudEventsJsonVersion")

	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("io.mockk:mockk:$mockkVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2")
	testImplementation("org.springframework.kafka:spring-kafka-test:$kafkaVersion")

	testImplementation("io.kotest:kotest-runner-junit5:$kotestJunit5Version")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestSpringVersion")
	testImplementation("io.kotest:kotest-assertions-json:$kotestJsonVersion")

	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
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
				"**/InventoryServiceApplication*.*",
				"**/InventoryTableSchemaCreation*.*",
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
						"**/InventoryServiceApplication*.*",
						"**/InventoryTableSchemaCreation*.*",
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
	imageName = "shaikrasheed99/inventory-service:latest"
	builder = "paketobuildpacks/builder:base"
	environment = mapOf(
		"BP_JVM_VERSION" to "17"
	)
}
