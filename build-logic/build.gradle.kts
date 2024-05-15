plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori", "indra-common", "3.0.1")
    implementation("io.github.goooler.shadow", "shadow-gradle-plugin", "8.1.7")
    implementation("io.papermc.paperweight", "paperweight-userdev", "1.6.2")
}