plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.16.0"
    kotlin("jvm") version("1.5.31")
    `kotlin-dsl`
}

group = "info.pzss.zomboid"
version = "0.1.0"

pluginBundle {
    website = "https://github.com/pz-extender/pz-gradle"
    vcsUrl = "https://github.com/pz-extender/pz-gradle"
    tags = listOf("project-zomboid", "zomboid", "modding", "mod")
}

gradlePlugin {
    plugins {
        create("zomboidPlugin") {
            id = "info.pzss.zomboid.gradle"
            displayName = "Project Zomboid Plugin"
            description = "Use a Project Zomboid installation as a Java dependency"
            implementationClass = "info.pzss.zomboid.gradle.ProjectZomboidPlugin"
        }
    }
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