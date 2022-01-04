package info.pzss.zomboid.gradle

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.nio.file.Files
import java.nio.file.Paths


abstract class ProjectZomboidExtension {
    abstract val zomboidPath: Property<String>

    private enum class DistributionType {
        SERVER,
        CLIENT
    }

    private val distributonType: Provider<DistributionType> = zomboidPath.map {
        when (Files.exists(Paths.get(it, "java"))) {
            true -> DistributionType.SERVER
            false -> DistributionType.CLIENT
        }
    }

    val zomboidClasspathRoot: Provider<String> = zomboidPath.zip(distributonType) { path, type ->
        when (type) {
            DistributionType.CLIENT -> path
            DistributionType.SERVER -> "$path/java"
        }
    }
}