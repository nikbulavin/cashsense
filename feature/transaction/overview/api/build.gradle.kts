plugins {
    alias(libs.plugins.cashsense.android.feature.api)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.transaction.overview.api"
}

dependencies {
    api(projects.core.navigation)
}
