import info.pzss.zomboid.gradle.ProjectZomboidExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

@Suppress("unused", "ObjectPropertyName")
inline val PluginDependenciesSpec.`project-zomboid`: PluginDependencySpec
    get() = id("info.pzss.zomboid.gradle")

val Project.pzClasspathRoot
    get() = rootProject.the<ProjectZomboidExtension>().zomboidClasspathRoot

@Suppress("unused")
fun DependencyHandlerScope.pzGameApi() = project(":", configuration = "projectZomboid")

fun Project.pzGameLibs() = fileTree(pzClasspathRoot) {
    include("*.jar")
}.builtBy(project.rootProject.tasks.named("projectZomboidJar"))

@Suppress("unused")
fun Project.pzGameRuntime() =
    pzGameLibs().asFileTree + files(project.rootProject.the<ProjectZomboidExtension>().zomboidClasspathRoot)