pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Generates projects.core.domain and the like, checked at compile time.
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Without spaces, which the type-safe project accessors require.
rootProject.name = "AndroidRecruitmentTestApp"
include(":app")
include(":core:analytics")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":feature:albums:api")
include(":feature:albums:presentation")
include(":feature:albums:ui")
include(":feature:details:api")
include(":feature:details:presentation")
include(":feature:details:ui")
include(":feature:favorites:api")
include(":feature:favorites:presentation")
include(":feature:favorites:ui")
include(":libs:albums:ui")
