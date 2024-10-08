# Project Zomboid Gradle Plugin

## Unreleased

## 0.2.0 - 2024-09-15

### Breaking Changes

- Remove `pzLocal()` repository
- Game artifacts are now provided via outgoing configurations instead of a `flatRepository`
- Remove IntelliJ Run Configuration support. Note: these can still be created manually via Gradle tasks.

## 0.1.8 - 2022-12-18

### Fixes

- Add provided dependencies to classpath when creating IDEA run configurations.
- Provide zomboid dependency jars to Fernflower during decompilation step.

## 0.1.7 - 2022-12-16

### Fixes

- Use new API to create IDEA run configurations.

## 0.1.6

### Breaking Changes

- Minimum Java version is now 17
- Minimum Gradle version is now 7.3

## 0.1.5

### Fixes

- Specify a group name for project-zomboid artifact

## 0.1.4

### Fixes

- Use correct classpath root for launch tasks

## 0.1.3

### Changed

- Don't require specifying game and server path separately

### Fixes

- Apply IDEA and IDEA-ext plugins automatically

## 0.1.2

### Added

- Create IntelliJ IDEA run configurations for each launcher task.

## 0.1.1

### Changed

- Drop dependency on Kotlin Gradle plugin.

## 0.1.0

### Added

- Initial support for local PZ repository/artifacts.
- Task type to launch Project Zomboid.
