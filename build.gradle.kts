group = "fr.ladder"
version = "1.0.0"

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")

    version = rootProject.version

    repositories {
        mavenCentral()
        maven {
            name = "maven-releases"
            url = uri("https://repo.lylaw.fr/repository/maven-releases/")
            credentials {
                username = findProperty("NEXUS_USER") as String?
                password = findProperty("NEXUS_PASSWORD") as String?
            }
        }
    }
}
