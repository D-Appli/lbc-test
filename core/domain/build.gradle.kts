import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Kotlin Gradle plugin is already on the buildscript classpath (root build.gradle.kts),
    // so the plugin is applied without a version.
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.paging.common)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
