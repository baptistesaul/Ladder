plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fr.ladder.core"

tasks.compileJava {
    options.encoding = "UTF-8"
}

dependencies {
    implementation("org.jetbrains", "annotations", "24.0.1")
    implementation("org.reflections", "reflections", "0.10.2")
    implementation("org.slf4j", "slf4j-simple", "1.6.1")
    implementation(project(":Ladder-API"))

    compileOnly("fr.snowtyy", "papermc", "1.8.8")
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    filesMatching("**/*.yml") {
        filter {
            it.replace("@version@", version.toString())
        }
    }
    outputs.upToDateWhen { false }
}