plugins {
    id("erosion.bukkit-conventions")
}

dependencies {
    api(projects.bukkitCommon) {
        isTransitive = false
    }
    paperweight.paperDevBundle("1.20.5-R0.1-SNAPSHOT")
}

paperweight {
    reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}