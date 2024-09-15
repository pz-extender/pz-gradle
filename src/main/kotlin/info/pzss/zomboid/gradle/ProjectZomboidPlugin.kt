package info.pzss.zomboid.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.ide.idea.IdeaPlugin

@Suppress("unused")
open class ProjectZomboidPlugin : Plugin<Project> {
    override fun apply(project: Project) = project.run {
        if (this != rootProject) {
            error("info.pzss.zomboid should only be applied to the root project")
        }

        plugins.apply(JavaPlugin::class)

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
}
