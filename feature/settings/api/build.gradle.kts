plugins {
    alias(libs.plugins.cashsense.android.feature.api)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.settings.api"
}

dependencies {
    api(projects.core.navigation)
}
