package info.pzss.zomboid.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.FAILED
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ProjectZomboidPluginTest {

    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File
    private lateinit var propertiesFile: File

    @BeforeEach
    fun setup() {
        settingsFile = File(testProjectDir, "settings.gradle.kts")
        propertiesFile = File(testProjectDir, "gradle.properties")
        buildFile = File(testProjectDir, "build.gradle.kts")
    }

    @Test
    fun `decompiles sources`() {
        val buildFileContent = """
         plugins {
            id("info.pzss.zomboid")
         }
         
         projectZomboid {
             zomboidPath.set("/home/gtierney/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid")
         }
      """.trimIndent()

        buildFile.writeText(buildFileContent)


        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments(":projectZomboidSourcesJar")
            .withDebug(true)
            .run()

        assertEquals(SUCCESS, result.task(":projectZomboidSourcesJar")?.outcome)
    }
}