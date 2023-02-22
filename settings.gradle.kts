rootProject.name = "Erosion"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()

        maven("https://repo.opencollab.dev/main")

        // ViaVersion
        maven("https://repo.viaversion.com") {
            name = "viaversion"
        }

        maven("https://repo.papermc.io/repository/maven-public")
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
include("bukkit-plugin")
