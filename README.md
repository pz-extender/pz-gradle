# pz-gradle

pz-gradle is a plugin for Gradle that allows you to use a Project Zomboid installation as a dependency in Gradle.

**NOTE**: This plugin is not yet available on the Gradle Plugin Portal for usage.

## Usage

The plugin must be registered in the root project (i.e. top-level `build.gradle.kts`).

```kotlin
plugins {
    id("info.pzss.zomboid") version ("0.1.1")
}

projectZomboid {
    gamePath = "C:/Program Files (x86)/Steam/steamapps/common/ProjectZomboid"
}

repositories {
    pzLocal()
}

dependencies {
    compileOnly(pzGameApi())
}
```

### Running a Project Zomboid installation as a Gradle task

The plugin also registers a new task type: `info.pzss.zomboid.gradle.ProjectZomboidLaunchTask`.
This can be used to configure and launch Project Zomboid with custom settings.

```kotlin
tasks.register<ProjectZomboidLaunchTask> {
   launchType.set(LaunchType.CLIENT)
   additionalJvmArgs.set(listOf("-javaagent:my-pz-agent.jar"))
}
```