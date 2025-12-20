plugins {
    alias(libs.plugins.cashsense.android.feature.api)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.transaction.dialog.api"
}

dependencies {
    api(projects.core.navigation)

    implementation(projects.feature.wallet.detail.api)
}
