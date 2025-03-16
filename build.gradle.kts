plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"  // Spring 관련 플러그인 추가
    kotlin("plugin.jpa") version "1.9.21"     // JPA 플러그인 추가
    kotlin("kapt") version "1.9.21"           // Lombok 및 Annotation Processor 활성화
}

group = "yin"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.kapt") // Lombok 활성화

    repositories {
        mavenCentral()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        // Spring Boot Core
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-web")

        // Spring Data JPA & Database
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        runtimeOnly("com.h2database:h2") // H2 추가

        // Lombok (kapt을 사용해야 함)
        compileOnly("org.projectlombok:lombok")
        kapt("org.projectlombok:lombok")

        // Kotlin 관련
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

        // Validation
        implementation("org.springframework.boot:spring-boot-starter-validation")

        // 테스트
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}
