dependencies {
    api(projects.pluginCore)
    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
    implementation("xyz.jpenilla", "reflection-remapper", "0.1.0-SNAPSHOT")

    implementation("org.geysermc.geyser.adapters", "spigot-all", "1.6-SNAPSHOT")

    compileOnly("com.viaversion", "viaversion", "4.5.0")
}

application {
    mainClass.set("org.geysermc.erosion.bukkit.BukkitPlugin")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("Erosion")
}