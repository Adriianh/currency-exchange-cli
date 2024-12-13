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

    implementation("com.github.ajalt.clikt:clikt-markdown:5.0.1") /* Optional */

    /* Kotter */
    implementation("com.varabyte.kotter:kotter-jvm:1.2.0")

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