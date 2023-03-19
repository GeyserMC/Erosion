plugins {
    id("erosion.publish-conventions")
}

dependencies {
    api("io.netty", "netty-transport", "4.1.43.Final")
    api("io.netty", "netty-handler", "4.1.43.Final")
    api("io.netty", "netty-transport-native-epoll", "4.1.43.Final")
    // TODO just needed for VarInt encoding. Might be wholly unnecessary.
    api("com.nukkitx.network", "common", "1.6.25")
    // TODO use release.
    api("com.github.steveice10", "opennbt", "1.5-SNAPSHOT")
    api("com.nukkitx", "math", "1.1.1")

    api("com.nukkitx.fastutil", "fastutil-int-object-maps", "8.5.3")
    api("com.nukkitx.fastutil", "fastutil-object-int-maps", "8.5.3")
    implementation("org.jetbrains:annotations:20.1.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
}