pluginManagement {
    resolutionStrategy {
        eachPlugin {
            val plugin = requested.id.id
            when (plugin) {
                "kotlinx-serialization" -> useModule("org.jetbrains.kotlin:kotlin-serialization:1.3.61")
            }
        }
    }
}
enableFeaturePreview("GRADLE_METADATA")
include("common")
include("androidApp")