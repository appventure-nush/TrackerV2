import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.7.20"
    id("org.jetbrains.compose") version "1.5.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material3:material3-desktop:1.5.0")
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("com.github.ajalt.colormath:colormath:3.2.1")
    implementation("org.bytedeco:javacv-platform:1.5.7")
    implementation("com.github.holgerbrandl:krangl:0.18")
    implementation("com.godaddy.android.colorpicker:compose-color-picker-jvm:0.5.1")
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-jdk8"))
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)

            windows {
                iconFile.set(File("src/main/resources/trackerv2.ico"))
            }
            macOS {
                iconFile.set(File("src/main/resources/trackerv2.icns"))
            }
            linux {
                iconFile.set(File("src/main/resources/trackerv2.png"))
            }
        }
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}