plugins {
    alias(libs.plugins.cashsense.android.feature.impl)
    alias(libs.plugins.cashsense.android.library.compose)
    alias(libs.plugins.aboutlibraries)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.settings.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.locales)

    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)
    implementation(libs.androidx.browser)
}