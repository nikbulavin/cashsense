plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.subscription.dialog.impl"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.accompanist.permissions)
}