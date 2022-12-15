package info.pzss.zomboid.gradle

import com.google.gson.Gson
import groovy.lang.Closure
import org.gradle.api.Task
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.closureOf
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import java.io.File

abstract class ProjectZomboidLaunchTask : JavaExec() {

    @InputFile
    fun getLaunchSettingsFile(): Provider<File> {
        return project.rootProject.extensions
            .getByType<ProjectZomboidExtension>()
            .zomboidPath
            .zip(launchSettings) { zomboid, launchSettings -> File(zomboid, launchSettings) }
    }

    @get:Input
    abstract val additionalJvmArgs: ListProperty<String>

    @get:Input
    abstract val launchSettings: Property<String>

    init {
        launchSettings.convention("ProjectZomboid64.json")
    }


    fun configureAfterEvaluate(gameSettings: ProjectZomboidExtension) {
        val gamePathProp = gameSettings.zomboidPath

        if (!gamePathProp.isPresent) {
            return
        }

        val gamePath = gamePathProp.get()
        val launchSettingsFile = getLaunchSettingsFile()
        val launchSettings = Gson().fromJson(launchSettingsFile.get().readText(), ProjectZomboidSettings::class.java)

        jvmArgs = launchSettings.vmArgs + additionalJvmArgs.get()
        mainClass.set(launchSettings.mainClass.replace('/', '.'))
        classpath += project.files(*launchSettings.classpath
            .map { File(gamePath, it) }
            .toTypedArray())

        workingDir(gamePath)
    }
}
