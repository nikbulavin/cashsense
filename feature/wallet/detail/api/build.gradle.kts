plugins {
    alias(libs.plugins.cashsense.android.feature.api)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.wallet.detail.api"
}

dependencies {
    api(projects.core.navigation)

    implementation(projects.feature.home.api)
}
