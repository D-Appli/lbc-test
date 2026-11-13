plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.screenshot)
}

android {
    namespace = "fr.leboncoin.feature.favorites.ui"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    // The navigation entry takes a callback with an Album.
    api(projects.core.domain)
    implementation(projects.feature.favorites.api)
    // Hilt aggregates the @HiltViewModel classes from the runtime classpath of app.
    implementation(projects.feature.favorites.presentation)
    implementation(projects.libs.albums.ui)

    implementation(libs.bundles.feature.ui)
    // The entry is an extension of EntryProviderScope.
    api(libs.androidx.navigation3.runtime)
    implementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    implementation(platform(libs.spark.bom))
    implementation(libs.bundles.compose.ui)

    screenshotTestImplementation(libs.bundles.screenshot.test)
}
