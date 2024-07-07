plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "org.zwylair.violetproject"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        setUrl("https://jitpack.io")
    }
}

dependencies {
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("ch.qos.logback:logback-classic:1.5.6")
}

application {
    mainClass.set("MainKt")
}

kotlin {
    jvmToolchain(17)
}