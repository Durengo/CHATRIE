plugins {
    id("java")
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("plugin.spring") version "1.9.22"
}

group = "lt.viko.eif.pi21e"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // LOGGING
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.12")
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
     implementation("ch.qos.logback:logback-classic:1.4.14")

    // TESTING
    // https://mvnrepository.com/artifact/junit/junit
    testImplementation("junit:junit:4.13.2")

    // SPRING BOOT
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.data:spring-data-envers")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security
    implementation("org.springframework.boot:spring-boot-starter-security:3.2.3")

    // Cassandra
    implementation("org.springframework.boot:spring-boot-starter-data-cassandra")

    // RabbitMQ
    implementation("org.springframework.boot:spring-boot-starter-amqp")
}

tasks.test {
    useJUnitPlatform()
}