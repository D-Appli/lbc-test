plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.screenshot)
}

android {
    namespace = "fr.leboncoin.feature.albums.ui"
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
    implementation(projects.feature.albums.api)
    // Hilt aggregates the @HiltViewModel classes from the runtime classpath of app.
    implementation(projects.feature.albums.presentation)
    implementation(projects.libs.albums.ui)

    implementation(libs.bundles.feature.ui)
    // The entry is an extension of EntryProviderScope.
    api(libs.androidx.navigation3.runtime)
    implementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    // Pull to refresh, which Spark does not provide; the version comes from the Compose BOM.
    implementation(libs.androidx.material3)
    implementation(platform(libs.spark.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.paging.compose)

    screenshotTestImplementation(libs.bundles.screenshot.test)
}
