plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "ru.decahthuk"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2024.1")
    plugins.set(listOf("com.intellij.java"))
}

tasks {

    // TODO: Make branch for 2022.1 and set appropriate props. org.jetbrains.intellij - 1.9.0, lombok 1.18.30
    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("243.*")
    }

    test {
        systemProperty("idea.force.use.core.classloader", "true")
        jvmArgs("-Djava.system.class.loader=com.intellij.util.lang.PathClassLoader")
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
