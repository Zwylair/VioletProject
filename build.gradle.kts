plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "org.zwylair.violetproject"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("dev.inmo:tgbotapi:15.0.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")
}

application {
    mainClass.set("org.zwylair.violetproject.MainKt")
}
