plugins {
    `java-library`
    //id("net.kyori.indra")
}

dependencies {
    compileOnly("org.checkerframework", "checker-qual", "3.19.0")
}

//indra {
//    github("GeyserMC", "Erosion") {
//        ci(true)
//        issues(true)
//        scm(true)
//    }
//    mitLicense()
//
//    javaVersions {
//        target(8)
//    }
//}

tasks {
    processResources {
        // Spigot, BungeeCord, Velocity, Sponge, Fabric
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

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
}