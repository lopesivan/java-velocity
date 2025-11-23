plugins {
    kotlin("jvm") version "2.0.0"
    application
}

group = "com.exemplo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Velocity
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")

    // SLF4J
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

kotlin {
    jvmToolchain(17)
}

application {
    // ATENÇÃO: nome da classe gerada a partir do Main.kt
    mainClass.set("com.exemplo.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

