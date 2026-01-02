plugins {
    alias(libs.plugins.cashsense.android.feature.api)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.transaction.detail.api"
}

dependencies {
    api(projects.core.navigation)
}
