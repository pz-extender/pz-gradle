import info.pzss.zomboid.gradle.ProjectZomboidExtension
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

inline val PluginDependenciesSpec.`project-zomboid`: PluginDependencySpec
    get() = id("info.pzss.zomboid.gradle")

@Deprecated("Deprecated since 0.1.1", replaceWith = ReplaceWith("pzLocal()"))
fun Project.pzLocalRepository() = pzLocal()

fun Project.pzLocal() = repositories.flatDir {
    dirs(project.rootProject.tasks.getByName<Jar>("projectZomboidSourcesJar").destinationDirectory)
}

val Project.pzGamePath
    get() = rootProject.the<ProjectZomboidExtension>().gamePath

fun Project.pzGameApi() = mapOf("name" to "project-zomboid", "version" to "latest")
fun Project.pzGameLibs() = fileTree(pzGamePath) {
    include("*.jar")
}

fun Project.pzGameRuntime() = fileTree(pzGamePath) {
    include("*.jar")
    include(".")
}