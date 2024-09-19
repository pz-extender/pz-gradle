plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.3.0"
    id("org.jetbrains.changelog") version "2.2.1"
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

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

gradlePlugin {
    website.set("https://github.com/pz-extender/pz-gradle")
    vcsUrl.set("https://github.com/pz-extender/pz-gradle")

    plugins {
        create("zomboidPlugin") {
            id = "info.pzss.zomboid"
            displayName = "Project Zomboid Plugin"
            description = "Use a Project Zomboid installation as a Java dependency"
            implementationClass = "info.pzss.zomboid.gradle.ProjectZomboidPlugin"
            tags.set(listOf("project-zomboid", "zomboid", "modding", "mod"))
        }
    }
}

repositories {
    mavenCentral()
    google()
    maven("https://www.jetbrains.com/intellij-repository/releases/")
    maven("https://plugins.gradle.org/m2")
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.jetbrains.intellij.java:java-decompiler-engine:242.22855.74")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(gradleTestKit())
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}