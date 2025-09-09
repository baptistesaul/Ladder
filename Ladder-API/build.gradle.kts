group = "fr.ladder.api"

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs = listOf("-parameters")
}

dependencies {
    implementation("org.jetbrains", "annotations", "24.0.1")
    implementation("org.slf4j", "slf4j-simple", "1.6.1")

    compileOnly("fr.snowtyy", "papermc", "1.8.8")
}