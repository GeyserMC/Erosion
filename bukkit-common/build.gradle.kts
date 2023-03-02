plugins {
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "org.geysermc.erosion" //project.group as String
            artifactId = "bukkit-common" //project.name
            version = "1.0-SNAPSHOT" //project.version as String

            from(components["java"])
        }
    }
}

dependencies {
    api(projects.common)
    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}