import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByName
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

inline val PluginDependenciesSpec.`project-zomboid`: PluginDependencySpec
    get() = id("info.pzss.zomboid.gradle")

fun Project.pzLocalRepository() = repositories.flatDir {
    dirs(project.rootProject.tasks.getByName<Jar>("projectZomboidSourcesJar").destinationDirectory)
}

fun DependencyHandler.pzGameApi() = mapOf("name" to "project-zomboid", "version" to "latest")