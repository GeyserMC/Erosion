plugins {
    java
    id("erosion.build-logic")
}

allprojects {
    group = "org.geysermc.erosion"
    version = "1.1-SNAPSHOT"
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

    when (this) {
        in platforms -> plugins.apply("erosion.shadow-conventions")
    }
}