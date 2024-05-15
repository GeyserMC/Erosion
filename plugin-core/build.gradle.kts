dependencies {
    api(projects.common)
    api("org.geysermc.configutils", "configutils", "1.0-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}