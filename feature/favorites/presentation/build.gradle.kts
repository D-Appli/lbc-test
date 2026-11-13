plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "fr.leboncoin.feature.favorites.presentation"
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
    // The ViewModel exposes the domain models.
    api(projects.core.domain)

    // ViewModel and ImmutableList are part of the public API of FavoritesViewModel.
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.kotlinx.collections.immutable)
    // @Immutable on the UI state, which only needs the annotation and not the Compose runtime.
    implementation(libs.androidx.compose.runtime.annotation)

    implementation(libs.timber)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.bundles.unit.test)
    // AlbumRepository exposes PagingData, for the fake of the tests.
    testImplementation(libs.paging.common)
}
