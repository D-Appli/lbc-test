import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

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
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.screenshot) apply false
    alias(libs.plugins.dependency.analysis)
}

dependencyAnalysis {
    issues {
        all {
            // The transitive dependencies used directly are details of Compose, Spark, Hilt and Coil;
            // declaring them would only add versions to maintain.
            onUsedTransitiveDependencies { severity("ignore") }
            // The same class in two dependencies makes the build depend on the classpath order.
            onDuplicateClassWarnings { severity("fail") }
        }
    }
}

subprojects {
    // Reports the unused, misused and undeclared dependencies of each module, with buildHealth.
    apply(plugin = "com.autonomousapps.dependency-analysis")

    pluginManager.withPlugin("org.jetbrains.kotlin.plugin.compose") {
        extensions.configure<ComposeCompilerGradlePluginExtension> {
            stabilityConfigurationFiles.add(
                rootProject.layout.projectDirectory.file("compose_stability.conf"),
            )
        }
    }
}
