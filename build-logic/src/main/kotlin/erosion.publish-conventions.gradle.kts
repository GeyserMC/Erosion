plugins {
    id("net.kyori.indra.publishing")
}

indra {
    publishSnapshotsTo("geysermc", "https://repo.opencollab.dev/maven-snapshots")
    publishReleasesTo("geysermc", "https://repo.opencollab.dev/maven-releases")

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                groupId = project.group as String
                artifactId = project.name
                version = project.version as String

                from(components["java"])
            }
        }
    }
}