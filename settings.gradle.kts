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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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
include(":core:navigation")
include(":core:network")
include(":core:notifications")
include(":core:shortcuts")
include(":core:ui")

include(":feature:category:detail:api")
include(":feature:category:detail:impl")
include(":feature:category:editor:api")
include(":feature:category:editor:impl")
include(":feature:category:list:api")
include(":feature:category:list:impl")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:settings:api")
include(":feature:settings:impl")
include(":feature:subscription:dialog:api")
include(":feature:subscription:dialog:impl")
include(":feature:subscription:list:api")
include(":feature:subscription:list:impl")
include(":feature:transaction:detail:api")
include(":feature:transaction:detail:impl")
include(":feature:transaction:editor:api")
include(":feature:transaction:editor:impl")
include(":feature:transaction:importer:api")
include(":feature:transaction:importer:impl")
include(":feature:transaction:overview:api")
include(":feature:transaction:overview:impl")
include(":feature:transfer:api")
include(":feature:transfer:impl")
include(":feature:wallet:dialog:api")
include(":feature:wallet:dialog:impl")
include(":feature:wallet:widget")

include(":work")