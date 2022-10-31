plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.19.0"
    id("org.jetbrains.changelog") version "2.0.0"
    `kotlin-dsl`
}

fun properties(key: String) = project.findProperty(key)?.toString()

description = properties("description")
group = properties("projectGroup")!!
version = properties("version")!!

changelog {
    version.set(project.version as? String)
    groups.set(emptyList())
}

pluginBundle {
    website = "https://github.com/pz-extender/pz-gradle"
    vcsUrl = "https://github.com/pz-extender/pz-gradle"
    tags = listOf("project-zomboid", "zomboid", "modding", "mod")
}

gradlePlugin {
    plugins {
        create("zomboidPlugin") {
            id = "info.pzss.zomboid"
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
    implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:1.1.1")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.openrs2:fernflower:1.1.1")
}