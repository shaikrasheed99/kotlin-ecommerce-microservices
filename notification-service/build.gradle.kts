import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.0"
	id("io.spring.dependency-management") version "1.1.4"
	id("io.gitlab.arturbosch.detekt") version "1.23.1"

	kotlin("jvm") version "1.9.20"
	kotlin("plugin.spring") version "1.9.20"
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

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.fasterxml.jackson.core:jackson-databind")

	implementation("org.springframework.kafka:spring-kafka")

	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")

	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("io.mockk:mockk:1.13.9")
	testImplementation("com.h2database:h2")

	testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
	testImplementation("io.kotest:kotest-assertions-json:4.6.3")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
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
