package info.pzss.zomboid.gradle

import org.gradle.api.Plugin
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaProject
import org.jetbrains.gradle.ext.*

@Suppress("unused")
open class ProjectZomboidPlugin : Plugin<Project> {
    override fun apply(project: Project) = project.run {
        if (this != rootProject) {
            error("info.pzss.zomboid should only be applied to the root project")
        }

        plugins.apply(JavaPlugin::class)
        plugins.apply(IdeaPlugin::class)
        plugins.apply(IdeaExtPlugin::class)

        val config = extensions.create("projectZomboid", ProjectZomboidExtension::class)
        val pzLibsDir = layout.buildDirectory.dir("pz-libs")

        val projectZomboidJar by tasks.registering(Jar::class) {
            from(config.zomboidClasspathRoot)
            include("**/*.class")
            archiveFileName.set("project-zomboid-latest.jar")
            destinationDirectory.set(pzLibsDir)
        }

        val pzSources = tasks.register<ProjectZomboidDecompileTask>("projectZomboidSources") {
            inputJar.set(projectZomboidJar.flatMap { it.archiveFile })
            dependencyDir.set(config.zomboidClasspathRoot)
        }

        val projectZomboidSourcesJar by tasks.registering(Jar::class) {
            from(pzSources.flatMap { it.outputDirectory })
            include("**/*.java")
            archiveFileName.set("project-zomboid-latest-sources.jar")
            archiveClassifier.set("sources")
            destinationDirectory.set(pzLibsDir)
        }

        val projectZomboid by project.configurations.creating {
            isCanBeConsumed = true
            isCanBeResolved = true
        }

        artifacts {
            add("projectZomboid", projectZomboidJar)
            add("projectZomboid", projectZomboidSourcesJar)
        }

        subprojects {
            afterEvaluate {
                tasks.withType<ProjectZomboidLaunchTask> {
                    configureAfterEvaluate(config)
                }
            }
        }
    }

    private fun ProjectSettings.configureSyncTasks(vararg tasks: TaskProvider<*>) {
        taskTriggers {
            afterSync(*tasks)
        }
    }

    private fun ProjectSettings.configureRunConfigurations(project: Project) {
        val tasks = project.tasks.withType<ProjectZomboidLaunchTask>().filter { it.mainClass.isPresent }

        runConfigurations {
            tasks.forEach {
                createPzApp(it)
            }
        }
    }

    private fun PolymorphicDomainObjectContainer<RunConfiguration>.createPzApp(task: ProjectZomboidLaunchTask) {
        create<Application>(task.name) {
            includeProvidedDependencies = true
            workingDirectory = task.workingDir.toString()
            programParameters = "-debug"
            mainClass = task.mainClass.get()
            moduleRef(
                task.project,
                task.project.the<SourceSetContainer>().getByName("main")
            )
            jvmArgs = task.allJvmArgs.joinToString(
                " ",
                transform = { argument ->
                    "\"" + argument.replace("\\", "\\\\")
                        .replace("\t", "\\t")
                        .replace("\b", "\\b")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\'", "\\'")
                        .replace("\"", "\\\"") + "\""
                }
            )
        }
    }

    private fun IdeaProject.settings(
        action: ProjectSettings.() -> Unit,
    ) = (this as ExtensionAware).extensions.configure("settings", action)

    private fun ProjectSettings.taskTriggers(
        action: TaskTriggersConfig.() -> Unit,
    ) = (this as ExtensionAware).extensions.configure("taskTriggers", action)

    private fun ProjectSettings.runConfigurations(configuration: PolymorphicDomainObjectContainer<RunConfiguration>.() -> Unit) =
        (this as ExtensionAware).configure<RunConfigurationContainer> {
            apply(configuration)
        }
}
