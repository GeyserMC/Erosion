dependencies {
    api(projects.common)
    api("org.geysermc.configutils", "configutils", "1.0-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}