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

include(":feature:category:dialog:impl")
include(":feature:category:list:impl")
include(":feature:home:impl")
include(":feature:settings:impl")
include(":feature:subscription:dialog:impl")
include(":feature:subscription:list:impl")
include(":feature:transaction:dialog:impl")
include(":feature:transaction:overview:impl")
include(":feature:transfer:impl")
include(":feature:wallet:detail:impl")
include(":feature:wallet:dialog:impl")
include(":feature:wallet:widget:impl")

include(":work")