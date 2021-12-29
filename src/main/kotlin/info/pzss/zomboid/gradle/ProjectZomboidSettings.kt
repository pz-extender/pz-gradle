package info.pzss.zomboid.gradle

data class ProjectZomboidSettings(
    val mainClass: String,
    val classpath: List<String>,
    val vmArgs: List<String>
)
