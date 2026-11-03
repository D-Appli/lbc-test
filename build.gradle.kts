buildscript {
    dependencies {
        // AGP 9 bundles Kotlin Gradle plugin 2.2.10 for built-in Kotlin.
        // Declaring it here upgrades the Kotlin compiler to the catalog version.
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
}
