plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
    alias(libs.plugins.aboutlibraries)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.settings"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.locales)

    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)
    implementation(libs.androidx.browser)
}