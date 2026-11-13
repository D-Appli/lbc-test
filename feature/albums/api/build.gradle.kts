plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "fr.leboncoin.feature.albums.api"
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
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    // The routes are @Serializable; aligned with the version of the rest of the app.
    api(libs.kotlin.serialization.core)
}
