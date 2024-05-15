plugins {
    id("erosion.publish-conventions")
}

dependencies {
    api(projects.common)
    compileOnly("dev.folia", "folia-api", "1.19.4-R0.1-SNAPSHOT") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
    compileOnly("com.viaversion", "viaversion", "4.10.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}