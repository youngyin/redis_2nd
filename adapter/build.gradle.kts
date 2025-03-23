plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	kotlin("jvm")
	kotlin("plugin.spring")
	id("org.jetbrains.kotlin.plugin.jpa") version "1.9.21"
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation(project(":domain"))
	implementation(project(":application"))
	implementation(project(":infrastructure"))
}

tasks.bootJar {
	enabled = false
}

tasks.jar {
	enabled = true
}
