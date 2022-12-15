# pz-gradle

pz-gradle is a plugin for Gradle that allows you to use a Project Zomboid installation as a dependency in Gradle.

## Usage

The plugin must be registered in the root project (i.e. top-level `build.gradle.kts`).

```kotlin
plugins {
    id("info.pzss.zomboid") version ("0.1.2")
}

projectZomboid {
    gamePath.set("C:/Program Files (x86)/Steam/steamapps/common/ProjectZomboid")
}

repositories {
    pzLocal()
}

dependencies {
    compileOnly(pzGameApi())
    compileOnly(pzGameLibs())
}
```

### Running a Project Zomboid installation as a Gradle task

The plugin also registers a new task type: `info.pzss.zomboid.gradle.ProjectZomboidLaunchTask`. This can be used to
configure and launch Project Zomboid with custom settings.

For every `ProjectZomboidLaunchTask` a corresponding IntelliJ run configuration will be created.

```kotlin
tasks.register<ProjectZomboidLaunchTask>("pzLaunch64") {
    // optional
    additionalJvmArgs.set(listOf("-javaagent:my-pz-agent.jar"))
    launchSettings.set("ProjectZomboid64.json")
}
```