import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.owasp.dependencycheck") version "12.1.0"

	kotlin("jvm") version "2.1.0"
	kotlin("plugin.spring") version "2.1.0"
}

group = "com.ecommerce"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

val eurekaClientVersion = "4.1.3"

extra["springCloudVersion"] = "2023.0.1"

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.springframework.cloud:spring-cloud-starter-gateway")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:$eurekaClientVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyCheck {
	analyzers {
		centralEnabled = false
	}
	suppressionFile = "./suppression-file.xml"
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
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

springBoot {
	buildInfo()
}

tasks.named<BootBuildImage>("bootBuildImage") {
	imageName = "shaikrasheed99/api-gateway:latest"
	builder = "paketobuildpacks/builder:base"
	environment = mapOf(
		"BP_JVM_VERSION" to "17"
	)
}
