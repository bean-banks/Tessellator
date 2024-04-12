plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    id("application")
    // JavaFX plugin for specifing JavaFX modules in the JavaFX element in this build file
    id("org.openjfx.javafxplugin") version "0.1.0"

    id("io.ktor.plugin") version "2.3.9"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")

    // Relevant Batik packages
    implementation("org.apache.xmlgraphics:batik-bridge:1.17")
    implementation("org.apache.xmlgraphics:batik-css:1.17")
    implementation("org.apache.xmlgraphics:batik-dom:1.17")
    implementation("org.apache.xmlgraphics:batik-gvt:1.17")
    implementation("org.apache.xmlgraphics:batik-parser:1.17")
    implementation("org.apache.xmlgraphics:batik-svggen:1.17")
    implementation("org.apache.xmlgraphics:batik-util:1.17")
    implementation("org.apache.xmlgraphics:batik-swing:1.17")

    // Dependency for handling json
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("tessellator.Main")
}

javafx {
    // version = "21.0.2"
    // using an older version so javafx can work on macos 10.15
    version = "18.0.1"
    // javafx.controls depends on javafx.base and javafx.graphics so they will
    // be added as dependencies too because of transitivity.
    modules = listOf("javafx.controls", "javafx.swing", "javafx.web")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }
}