plugins {
    java
    id("erosion.build-logic")
}

allprojects {
    group = "org.geysermc.erosion"
    version = "1.0-SNAPSHOT"
    description = "Offloads Geyser work to backend servers to save on memory consumption."

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

val platforms = setOf(
        projects.bukkitPlugin
).map { it.dependencyProject }

subprojects {
    apply {
        plugin("java-library")
        plugin("application")
        plugin("erosion.build-logic")
    }

    tasks {
        processResources {
            // Spigot, Sponge, Fabric
            filesMatching(listOf("plugin.yml", "META-INF/sponge_plugins.json", "fabric.mod.json")) {
                expand(
                        "id" to "erosion",
                        "name" to "Erosion",
                        "version" to project.version,
                        "description" to project.description,
                        "url" to "https://geysermc.org",
                        "author" to "GeyserMC"
                )
            }
        }
        compileJava {
            options.encoding = Charsets.UTF_8.name()
        }
    }

    when (this) {
        in platforms -> plugins.apply("erosion.shadow-conventions")
    }
}