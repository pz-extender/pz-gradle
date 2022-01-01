package info.pzss.zomboid.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaProject
import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.RunConfiguration
import org.jetbrains.gradle.ext.TaskTriggersConfig

open class ProjectZomboidPlugin : Plugin<Project> {
    override fun apply(project: Project) = project.run {
        if (this != rootProject) {
            error("info.pzss.zomboid should only be applied to the root project")
        }

        val config = extensions.create("projectZomboid", ProjectZomboidExtension::class)
        val pzLibsDir = layout.buildDirectory.dir("pz-libs")

        val pzJar = tasks.register<Jar>("projectZomboidJar") {
            from(config.gamePath)
            include("**/*.class")
            archiveFileName.set("project-zomboid-latest.jar")
            destinationDirectory.set(pzLibsDir)
        }

        val pzSources = tasks.register<ProjectZomboidDecompileTask>("projectZomboidSources") {
            inputJar.set(pzJar.flatMap { it.archiveFile })
        }

        val pzSourcesJar = tasks.register<Jar>("projectZomboidSourcesJar") {
            from(pzSources.flatMap { it.outputDirectory })
            include("**/*.java")
            archiveFileName.set("project-zomboid-latest-sources.jar")
            destinationDirectory.set(pzLibsDir)
        }

        plugins.withType<IdeaPlugin> {
            with(model) {
                project {
                    settings {
                        configureSyncTasks(pzSourcesJar)
                        configureRunConfigurations(subprojects, config)
                    }
                }
            }
        }

        Unit
    }

    private fun ProjectSettings.configureSyncTasks(vararg tasks: TaskProvider<*>) {
        taskTriggers {
            afterSync(*tasks)
        }
    }

    private fun ProjectSettings.configureRunConfigurations(subprojects: Set<Project>, config: ProjectZomboidExtension) {
        subprojects.forEach { subproject ->
            subproject.afterEvaluate {
                val tasks = project.tasks.withType<ProjectZomboidLaunchTask>()
                tasks.forEach { it.configureAfterEvaluate(config) }

                runConfigurations {
                    tasks.forEach {
                        createPzApp(it)
                    }
                }
            }
        }
    }

    private fun PolymorphicDomainObjectContainer<RunConfiguration>.createPzApp(task: ProjectZomboidLaunchTask) {
        create<Application>(task.name) {
            includeProvidedDependencies = false
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
        (this as ExtensionAware).configure<NamedDomainObjectContainer<RunConfiguration>> {
            (this as PolymorphicDomainObjectContainer<RunConfiguration>).apply(configuration)
        }
}
