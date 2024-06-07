val kotlin_version: String by project
val logback_version: String by project
val postgresql_driver_version: String by project
val exposed_version: String by project
val slf4j_version: String by project
val ktor_version: String by project

plugins {
    kotlin("jvm") version "2.0.0-RC3" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "2.0.0-RC3"
    id("io.ktor.plugin") version "2.3.11"
}

application {
    mainClass.set("io.taskrunner.RestApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor", "ktor-server-thymeleaf-jvm", "2.3.5")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.slf4j:slf4j-api:$slf4j_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    // postgresql libraries
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0-RC")
    implementation("org.postgresql:postgresql:$postgresql_driver_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    // rabbitmq
    implementation("com.rabbitmq:amqp-client:latest.release")
}
