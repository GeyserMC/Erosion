dependencies {
    api(projects.pluginCore)
    api(projects.bukkitCommon)
    compileOnly("dev.folia", "folia-api", "1.19.4-R0.1-SNAPSHOT") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
    implementation("xyz.jpenilla", "reflection-remapper", "0.1.0-SNAPSHOT")

    implementation("org.geysermc.geyser.adapters", "spigot-all", "1.9-SNAPSHOT", classifier="all")

    compileOnly("com.viaversion", "viaversion", "4.6.0")
}

relocate("it.unimi.dsi.fastutil")
relocate("net.fabricmc") // Provided by ReflectionRemapper
relocate("org.yaml")
relocate("xyz.jpenilla")

application {
    mainClass.set("org.geysermc.erosion.bukkit.BukkitPlugin")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
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
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}