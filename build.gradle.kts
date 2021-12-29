plugins {
    kotlin("jvm") version("1.5.31")
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    maven("https://repo.openrs2.org/repository/openrs2/")
    maven("https://plugins.gradle.org/m2")
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
    implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:1.1.1")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.openrs2:fernflower:1.1.1")
}