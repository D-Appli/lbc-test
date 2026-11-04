plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
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
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":feature:albums:api"))
    implementation(project(":feature:albums:presentation"))
    implementation(project(":libs:albums:ui"))

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    // Pull to refresh, which Spark does not provide; the version comes from the Compose BOM.
    implementation(libs.androidx.material3)
    implementation(platform(libs.spark.bom))
    implementation(libs.spark)
    implementation(libs.paging.compose)
}
