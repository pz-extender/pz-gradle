package info.pzss.zomboid.gradle

import org.gradle.api.provider.Property

abstract class ProjectZomboidExtension {
    abstract val gamePath: Property<String>
    abstract val serverPath: Property<String>
}