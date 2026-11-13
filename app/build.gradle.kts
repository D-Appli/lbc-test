plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val appVersionName = "0.2.0"

// major * 10000 + minor * 100 + patch, e.g. 1.2.3 gives 10203, so that it follows the version name.
val appVersionCode = appVersionName.split(".").map(String::toInt).let { (major, minor, patch) ->
    require(minor < 100 && patch < 100) { "The minor and patch versions must be below 100" }
    major * 10000 + minor * 100 + patch
}

android {
    namespace = "fr.leboncoin.androidrecruitmenttestapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "fr.leboncoin.androidrecruitmenttestapp"
        minSdk = 24
        targetSdk = 37
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(projects.core.analytics)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.feature.albums.api)
    implementation(projects.feature.albums.ui)
    implementation(projects.feature.details.api)
    implementation(projects.feature.details.ui)
    implementation(projects.feature.favorites.api)
    implementation(projects.feature.favorites.ui)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(platform(libs.spark.bom))
    implementation(libs.spark)

    // PhotoApp only builds the ImageLoader; the Compose integration is in libs:albums:ui.
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
    // PhotoApp injects the OkHttpClient of core:data, which uses OkHttp 5.
    implementation(libs.okhttp)
    implementation(libs.timber)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)

    debugImplementation(libs.androidx.ui.tooling)
    debugRuntimeOnly(libs.leakcanary.android)
}
