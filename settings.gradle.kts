pluginManagement {
    includeBuild("build-logic")
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
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "cashsense"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":baselineprofile")
include(":mobile")

include(":core:analytics")
include(":core:common")
include(":core:database")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:data")
include(":core:designsystem")
include(":core:domain")
include(":core:locales")
include(":core:model")
include(":core:network")
include(":core:notifications")
include(":core:shortcuts")
include(":core:ui")

include(":feature:category:dialog")
include(":feature:category:list")
include(":feature:home")
include(":feature:settings")
include(":feature:subscription:dialog")
include(":feature:subscription:list")
include(":feature:transaction:dialog")
include(":feature:transaction:overview")
include(":feature:transfer")
include(":feature:wallet:detail")
include(":feature:wallet:dialog")
include(":feature:wallet:widget")

include(":work")