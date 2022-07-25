import org.jetbrains.compose.compose

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.github.ajalt.colormath:colormath:3.2.0")
    implementation("org.bytedeco:javacv-platform:1.5.7")
    implementation("com.github.holgerbrandl:krangl:0.18")
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}