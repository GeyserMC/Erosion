plugins {
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "org.geysermc.erosion" //project.group as String
            artifactId = "common" //project.name
            version = "1.0-SNAPSHOT" //project.version as String

            from(components["java"])
        }
    }
}

dependencies {
    api("io.netty", "netty-transport", "4.1.43.Final")
    api("io.netty", "netty-handler", "4.1.43.Final")
    api("io.netty", "netty-transport-native-epoll", "4.1.43.Final")
    // TODO just needed for VarInt encoding. Might be wholly unnecessary.
    api("com.nukkitx.network", "common", "1.6.25")
    api("com.nukkitx", "nbt", "2.2.1")
    api("com.nukkitx", "math", "1.1.1")

    api("com.nukkitx.fastutil", "fastutil-int-object-maps", "8.5.3")
    api("com.nukkitx.fastutil", "fastutil-object-int-maps", "8.5.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}