plugins {
    alias(libs.plugins.cashsense.android.feature.api)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.transfer.dialog.api"
}

dependencies {
    api(projects.core.navigation)
}
