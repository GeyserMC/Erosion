import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("erosion.base-conventions")
    id("io.github.goooler.shadow")
}

tasks {
    named<Jar>("jar") {
        archiveClassifier.set("unshaded")
        from(project.rootProject.file("LICENSE"))
    }
    val shadowJar = named<ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        archiveVersion.set("")
        archiveClassifier.set("")

        val sJar: ShadowJar = this

        doFirst {
            providedDependencies[project.name]?.forEach { string ->
                sJar.dependencies {
                    println("Excluding $string from ${project.name}")
                    exclude(dependency(string))
                }
            }
        }
    }
    named("build") {
        dependsOn(shadowJar)
    }
}