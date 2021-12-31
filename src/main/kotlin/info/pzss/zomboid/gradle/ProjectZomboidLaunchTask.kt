package info.pzss.zomboid.gradle

import com.google.gson.Gson
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.getByType
import java.io.File


abstract class ProjectZomboidLaunchTask : JavaExec() {
    enum class LaunchType {
        SERVER,
        CLIENT
    }

    @InputFile
    fun getLaunchSettingsFile(): File {
        val gameSettings = project.rootProject.extensions.getByType<ProjectZomboidExtension>()
        val gamePath = gameSettings.gamePath.get()

        return File(gamePath, launchSettings.get())
    }

    @get:Input
    abstract val launchType: Property<LaunchType>

    @get:Input
    abstract val additionalJvmArgs: ListProperty<String>

    @get:Input
    abstract val launchSettings: Property<String>

    init {
        launchSettings.convention("ProjectZomboid64.json")
    }

    fun configureAfterEvaluate(gameSettings: ProjectZomboidExtension) {
        val gamePath = when(launchType.get()) {
            LaunchType.CLIENT -> gameSettings.gamePath.get()
            LaunchType.SERVER -> gameSettings.serverPath.get()
        }

        val launchSettingsFile = File(gamePath, launchSettings.get())
        val launchSettings = Gson().fromJson(launchSettingsFile.readText(), ProjectZomboidSettings::class.java)

        jvmArgs = launchSettings.vmArgs + additionalJvmArgs.get()
        mainClass.set(launchSettings.mainClass.replace('/', '.'))
        classpath += project.files(*launchSettings.classpath
            .map { File(gamePath, it) }
            .toTypedArray())

        workingDir(gamePath)
    }
}
