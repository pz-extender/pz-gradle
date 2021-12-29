package info.pzss.zomboid.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.plugins.ide.idea.model.IdeaProject
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.TaskTriggersConfig

open class ProjectZomboidPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val config = project.extensions.create("projectZomboid", ProjectZomboidExtension::class)
        val pzLibsDir = project.layout.buildDirectory.dir("pz-libs")

        val pzJar = project.tasks.register<Jar>("projectZomboidJar") {
            from(config.gamePath)
            include("**/*.class")
            archiveFileName.set("project-zomboid-latest.jar")
            destinationDirectory.set(pzLibsDir)
        }

        val pzSources = project.tasks.register<ProjectZomboidDecompileTask>("projectZomboidSources") {
            inputJar.set(pzJar.flatMap { it.archiveFile })
        }

        val pzSourcesJar = project.tasks.register<Jar>("projectZomboidSourcesJar") {
            from(pzSources.flatMap { it.outputDirectory })
            include("**/*.java")
            archiveFileName.set("project-zomboid-latest-sources.jar")
            destinationDirectory.set(pzLibsDir)
        }

        project.plugins.withType<IdeaPlugin> {
            with(model) {
                project {
                    settings {
                        taskTriggers {
                            afterSync(pzSourcesJar)
                        }
                    }
                }
            }
        }

        project.repositories {
            flatDir {
                dirs(project.tasks.getByName<Jar>("projectZomboidSourcesJar").destinationDirectory)
            }
        }

        project.afterEvaluate {
            val launchTasks = tasks.withType(ProjectZomboidLaunchTask::class)
            launchTasks.forEach { it.configureAfterEvaluate(config) }
        }
    }

    private fun IdeaProject.settings(
        action: ProjectSettings.() -> Unit,
    ) = (this as ExtensionAware).extensions.configure("settings", action)

    private fun ProjectSettings.taskTriggers(
        action: TaskTriggersConfig.() -> Unit,
    ) = (this as ExtensionAware).extensions.configure("taskTriggers", action)
}
