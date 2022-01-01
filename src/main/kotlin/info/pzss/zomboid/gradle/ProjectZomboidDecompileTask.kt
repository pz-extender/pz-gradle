package info.pzss.zomboid.gradle

import info.pzss.zomboid.gradle.tasks.decompile.DecompilerIo
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.java.decompiler.main.Fernflower
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences

abstract class ProjectZomboidDecompileTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:InputFile
    abstract val inputJar: RegularFileProperty

    init {
        outputDirectory.convention(project.rootProject.layout.buildDirectory.dir("zomboid-decompiled"))
    }

    @TaskAction
    fun execute() {
        val io = DecompilerIo(outputDirectory.get().asFile.toPath())
        val fernflowerOptions = mapOf<String, Any>(
            IFernflowerPreferences.DECOMPILE_ENUM to true,
            IFernflowerPreferences.MAX_PROCESSING_METHOD to "120",
        )

        val fernflower = Fernflower(io, io, fernflowerOptions, PrintStreamLogger(System.err))
        fernflower.addSource(inputJar.get().asFile)
        fernflower.decompileContext()
    }
}