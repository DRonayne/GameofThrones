pluginManagement {
    includeBuild("gradle/build-logic")
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

rootProject.name = "Game of Thrones"
include(":app")

// Core modules
include(":core:model")
include(":core:common")
include(":core:analytics")
include(":core:network")
include(":core:database")
include(":core:data")
include(":core:domain")
include(":core:ui")

// Feature modules
include(":feature:characters")
include(":feature:character-detail")
include(":feature:favorites")
include(":feature:comparison")
include(":feature:settings")
