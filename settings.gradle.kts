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

rootProject.name = "Android RecruitmentTest App"
include(":app")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":feature:albums:presentation")
include(":feature:albums:ui")
include(":feature:favorites:presentation")
include(":feature:favorites:ui")
