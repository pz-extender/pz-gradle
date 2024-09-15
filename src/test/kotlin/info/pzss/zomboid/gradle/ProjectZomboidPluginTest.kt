package info.pzss.zomboid.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.exists

@EnabledIf("zomboidInstallationExists")
class ProjectZomboidPluginTest {
    companion object {
        @JvmStatic
        fun zomboidInstallationExists(): Boolean {
            val path = System.getenv("ZOMBOID_PATH")?.let { Path(it) }
            return path?.exists() ?: false
        }
    }

    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    @BeforeEach
    fun setup() {
        settingsFile = File(testProjectDir, "settings.gradle.kts")
        buildFile = File(testProjectDir, "build.gradle.kts")
    }

    @AfterEach
    fun cleanup() {
        settingsFile.delete()
        buildFile.delete()
    }

    @Test
    fun `decompiles sources`() {
        val buildFileContent = """
         plugins {
            id("info.pzss.zomboid")
         }
         
         projectZomboid {
             zomboidPath.set(System.getenv("ZOMBOID_PATH"))
         }
      """.trimIndent()

        buildFile.writeText(buildFileContent)


        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments(":projectZomboidSourcesJar", "--stacktrace")
            .withDebug(true)
            .run()

        assertEquals(SUCCESS, result.task(":projectZomboidSourcesJar")?.outcome)
    }
}