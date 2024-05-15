// configuration for shading NMS implementations, but not adding them to any classpath, to avoid java version troubles
val shadowOnly: Configuration by configurations.creating

dependencies {
    api(projects.pluginCore)
    shadowOnly(projects.bukkitNms)
    api(projects.bukkitCommon)
    compileOnly("dev.folia", "folia-api", "1.19.4-R0.1-SNAPSHOT") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
    implementation("xyz.jpenilla", "reflection-remapper", "0.1.0-SNAPSHOT")

    implementation("org.geysermc.geyser.adapters", "spigot-all", "1.12-SNAPSHOT", classifier = "all")
    implementation("org.geysermc.geyser.adapters", "paper-all", "1.12-SNAPSHOT", classifier = "all")

    compileOnly("com.viaversion", "viaversion", "4.10.0")
}

relocate("it.unimi.dsi.fastutil")
relocate("net.fabricmc") // Provided by ReflectionRemapper
relocate("org.yaml")
relocate("xyz.jpenilla")

application {
    mainClass.set("org.geysermc.erosion.bukkit.BukkitPlugin")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    // Prevents Paper 1.20.5+ from remapping Erosion
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }

    archiveBaseName.set("Erosion")

    dependencies {
        exclude(dependency("io.netty:.*"))
        // From ConfigUtils
        exclude(dependency("org.checkerframework:checker-qual:.*"))
        exclude(dependency("com.google.code.findbugs:jsr305:.*"))
        exclude(dependency("com.google.errorprone:javac:.*"))
        exclude(dependency("com.github.spotbugs:spotbugs-annotations:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
    }

    configurations.add(shadowOnly)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}