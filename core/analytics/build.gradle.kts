plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "fr.leboncoin.core.analytics"
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
    // LocalAnalyticsHelper exposes a Compose type.
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    implementation(libs.javax.inject)
    implementation(libs.timber)
}
