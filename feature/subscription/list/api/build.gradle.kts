plugins {
    alias(libs.plugins.cashsense.android.feature.api)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.subscription.list.api"
}

dependencies {
    api(projects.core.navigation)
}
