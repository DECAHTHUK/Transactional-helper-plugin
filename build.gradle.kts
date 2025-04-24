plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.9.0"
}

group = "ru.decahthuk"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1")
    plugins.set(listOf("com.intellij.java"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("251.*")
    }

    test {
        systemProperty("idea.force.use.core.classloader", "true")
        jvmArgs("-Djava.system.class.loader=com.intellij.util.lang.PathClassLoader")
    }

    signPlugin {
        System.getenv("CERT_PATH")?.let { certificateChainFile.set(File(it)) }
        System.getenv("PRIVATE_KEY_PATH")?.let { privateKeyFile.set(File(it)) }
        System.getenv("PRIVATE_KEY_PASSWORD")?.let { password.set(it) }
    }


    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
