plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)

    application
}

repositories {
    mavenCentral()

    gradlePluginPortal()
}

dependencies {
    implementation(libs.guava)

    /* Clikt */
    implementation("com.github.ajalt.clikt:clikt:3.3.0")
    implementation("com.github.ajalt.clikt:clikt-markdown:5.0.1") /* Optional */

    /* Kotter */
    implementation("com.varabyte.kotter:kotter-jvm:1.2.1")

    /* OkHttp3 */
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))

    /* Serialization */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation(libs.serialization)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "com.github.adriianh.app.AppKt"
}

tasks {
    withType<Jar> {
        archiveBaseName.set("exchange")
        destinationDirectory.set(file("$rootDir/bin"))

        manifest {
            attributes["Main-Class"] = "com.github.adriianh.app.AppKt"
        }
        configurations["compileClasspath"].forEach { file: File ->
            from(zipTree(file.absoluteFile))
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    installDist {
        destinationDir = file("$rootDir/dist")
    }

    build {
        dependsOn(jar)
    }
}