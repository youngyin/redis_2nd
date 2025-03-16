plugins {
	kotlin("jvm")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
	kotlin("kapt") // Lombok 활성화
}

dependencies {
	implementation(project(":service-membership"))
	implementation(project(":service-movie"))
	implementation(project(":service-reservation"))
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
	mainClass.set("yin.bootstrap.BootstrapApplication")
}
