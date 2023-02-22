dependencies {
    api(projects.common)
    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
}

application {
    mainClass.set("org.geysermc.erosion.bukkit.BukkitPlugin")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("Erosion")
}