plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.screenshot)
}

android {
    namespace = "fr.leboncoin.feature.details.ui"
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
    implementation(projects.core.analytics)
    implementation(projects.core.domain)
    implementation(projects.feature.details.api)
    // Hilt aggregates the @HiltViewModel classes from the runtime classpath of app.
    implementation(projects.feature.details.presentation)
    implementation(projects.libs.albums.ui)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.bundles.feature.ui)
    // The entry is an extension of EntryProviderScope.
    api(libs.androidx.navigation3.runtime)
    implementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    // The transparent TopAppBar, which Spark does not provide.
    implementation(libs.androidx.material3)
    implementation(platform(libs.spark.bom))
    implementation(libs.bundles.compose.ui)

    screenshotTestImplementation(libs.bundles.screenshot.test)
}
