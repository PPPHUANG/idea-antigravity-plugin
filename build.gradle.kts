plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.example.antigravity"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configure Java Toolchain to use local JDK 20 as requested
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}
// CRITICAL: IntelliJ 2023.2+ runs on Java 17. 
// Even if we build with JDK 20, we MUST output Java 17 bytecode.
tasks.withType<JavaCompile> {
    options.release.set(17)
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2.6") // Target IDE version
    type.set("IC") // Target IDE type - IC (IntelliJ Community), IU (IntelliJ Ultimate)
}

tasks {
    // Set the JVM compatibility for the Kotlin task
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("255.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
