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
            // Any advice fails buildHealth, which the CI runs.
            onAny { severity("fail") }
            // The transitive dependencies used directly are details of Compose, Spark, Hilt and Coil;
            // declaring them would only add versions to maintain.
            onUsedTransitiveDependencies { severity("ignore") }
            // Not covered by onAny. The same class in two dependencies makes the build depend on
            // the classpath order.
            onDuplicateClassWarnings { severity("fail") }
        }
        // The exclusions below are deliberate: the advice was reviewed and declined.
        project(":core:analytics") {
            // Only for @Inject on public constructors.
            onIncorrectConfiguration { exclude(libs.javax.inject) }
        }
        project(":core:domain") {
            onIncorrectConfiguration { exclude(libs.javax.inject) }
        }
        project(":core:data") {
            // The standard dependency of a module with the Hilt plugin.
            onUnusedDependencies { exclude(libs.hilt.android) }
            // Exposed only by the public Hilt modules; the consumers do not use them.
            onIncorrectConfiguration {
                exclude(libs.kotlin.serialization.json, libs.retrofit.core)
                exclude(":core:database")
            }
        }
        project(":core:database") {
            // Exposed only by AppDatabase; the consumers go through the DAO.
            onIncorrectConfiguration { exclude(libs.room.runtime) }
        }
        project(":libs:albums:ui") {
            // For Modifier: exposing it would also need the Compose BOM as api, which every
            // consumer already declares.
            onIncorrectConfiguration { exclude(libs.androidx.ui) }
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
