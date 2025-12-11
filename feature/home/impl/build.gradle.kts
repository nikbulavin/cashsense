plugins {
    alias(libs.plugins.cashsense.android.feature.impl)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.home.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.locales)
    implementation(projects.feature.home.api)

    implementation(libs.androidx.compose.material3.adaptive.navigation3)
}