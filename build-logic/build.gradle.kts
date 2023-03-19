plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori", "indra-common", "3.0.1")
    implementation("gradle.plugin.com.github.johnrengelman", "shadow", "7.1.2")
}