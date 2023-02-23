rootProject.name = "Erosion"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()

        maven("https://repo.opencollab.dev/main")

        // ViaVersion
        maven("https://repo.viaversion.com") {
            name = "viaversion"
        }

        maven("https://repo.papermc.io/repository/maven-public")

        // Reflection-remapper
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

include("common")
include("plugin-core")
include("bukkit-common")
include("bukkit-plugin")
