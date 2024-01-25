import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.0"
	id("io.spring.dependency-management") version "1.1.4"

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
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
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
				"**/ProductServiceApplication*.*",
				"**/ProductsTableSchemaCreation*.*"
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
						"**/ProductServiceApplication*.*",
						"**/ProductsTableSchemaCreation*.*"
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
